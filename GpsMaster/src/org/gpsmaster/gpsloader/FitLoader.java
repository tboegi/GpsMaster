package org.gpsmaster.gpsloader;

/**
 * Created by KarstenEnsinger on 03.01.17.
 */

import javolution.io.Struct;
import org.apache.commons.io.FileUtils;
import org.gpsmaster.gpxpanel.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.xml.bind.ValidationException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class FitLoader extends GpsLoader {

    private ByteArrayInputStream bais = null;
    private int currentLoadColor = 0;

    /**
     * Encapsulates the FIT structure reading.
     */
    static class FitParser {
        private static final Integer TIMESTAMP_FIELD = 253;
        private static final int FILEID_MSG = 0;
        private static final Integer FILEID_FIELD_TYPE = 0;
        private static final Integer FILEID_FIELD_SERIAL = 3;
        private static final Integer FILEID_FIELD_CREATETIME = 4;
        private static final Integer FILEID_TYPE_ACTIVITY = 4;
        private static final int CREATOR_MSG = 49;
        private static final Integer CREATOR_FIELD_SVERSION = 0;
        private static final Integer CREATOR_FIELD_HVERSION = 1;
        private static final int HRV_MSG = 78;
        private static final Integer HRV_FIELD_TIME = 0;
        private static final Integer HRV_TIME_SCALE = 1000;
        private static final int RECORD_MSG = 20;
        private static final Integer RECORD_FIELD_TIMESTAMP = TIMESTAMP_FIELD;
        private static final Integer RECORD_FIELD_POS_LAT = 0;
        private static final Integer RECORD_FIELD_POS_LONG = 1;
        private static final Integer RECORD_FIELD_ALTITUDE = 2;
        private static final Integer RECORD_FIELD_HEARTRATE = 3;
        private static final Integer RECORD_FIELD_CADENCE = 4;
        private static final Integer RECORD_FIELD_ENHANCED_SPEED = 73;
        private static final Integer RECORD_FIELD_TEMPERATURE = 13;
        private static final Integer RECORD_FIELD_ZONE = 50;
        private static final Integer RECORD_ALTITUDE_SCALE = 5;
        private static final Integer RECORD_ALTITUDE_OFFSET = -500;
        private static final Integer RECORD_ENHANCED_SPEED_SCALE = 1000;
        private static final int LAP_MSG = 19;
        private static final Integer LAP_FIELD_INDEX = 254;
        private static final Integer LAP_FIELD_TIMESTAMP = TIMESTAMP_FIELD;
        private static final Integer LAP_FIELD_EVENT = 0;
        private static final Integer LAP_FIELD_EVENTTYPE = 1;
        private String creator = new String("");
        private String name = new String("");
        private Double lastHrv = 0.0;
        private ArrayList<ArrayList<FitRecord>> laps = new ArrayList<>(256);
        private ArrayList<FitRecord> records = new ArrayList<>(1024*256);
        private Integer lap = 1;

        public String getCreator() {
            return creator;
        }
        public String getName() {
            return name;
        }
        public Integer getLaps() {
            return lap;
        }
        public ArrayList<FitRecord> getLapRecords(final Integer lap) {
            return laps.get(lap);
        }
        /**
         * Convert a semicircle into degrees.
         * @param semicircle The semicircle to convert
         * @return The value in degree
         */
        private Double convertSemicirclesToDegree(final Integer semicircle) {
            Double degree;
            degree = semicircle * (180.0 / Math.pow(2, 31));
            return degree;
        }

        /**
         * Convert a value in degree to semicircle.
         * @param degree The value in degree to convert
         * @return The value as semicircle
         */
        private Integer convertDegreeToSemicircles(final Double degree) {
            Integer semicircle;
            // Let's assume, we never get a semicircle resulting in something
            // not capable to be displayed as an Integer. Hope springs eternal. ;-)
            semicircle = (int) Math.round(degree * (Math.pow(2, 31)));
            return semicircle;
        }

        /**
         * Calculate the CRC based on FIT specifications.
         */
        static final class Crc {
            private static final int[] crc_table = { 0x0000, 0xCC01, 0xD801, 0x1400, 0xF001, 0x3C00, 0x2800, 0xE401, 0xA001, 0x6C00, 0x7800, 0xB401, 0x5000, 0x9C01, 0x8801, 0x4400 };

            private int crc;

            public Crc() {
                reset();
            }

            public long getValue() {
                return crc;
            }

            public void reset() {
                crc = 0;
            }

            public void calculate(final byte b) {
                int tmp;
                // checksum of lower four bits
                tmp = crc_table[crc & 0x0F];
                crc = (crc >> 4) & 0x0FFF;
                crc = crc ^ tmp ^ crc_table[b & 0x0F];

                // checksum of upper four bits
                tmp = crc_table[crc & 0x0F];
                crc = (crc >> 4) & 0x0FFF;
                crc = crc ^ tmp ^ crc_table[(b >> 4) & 0x0F];
            }

            public void calculate(final ByteBuffer buffer) {
                for (int i = 0; i < buffer.capacity(); ++i) {
                    byte b = buffer.get(i);
                    calculate(b);
                }
            }

            public void calculate(final byte[] buffer) {
                for (byte aBuffer : buffer) {
                    calculate(aBuffer);
                }
            }
        }
        /**
         * Contains the structure of a FIT data message as declared by a FIT definition message.
         */
        static class FitDataMessage {
            private Integer globalMessageNumber;
            private Integer localMessageNumber;
            private ArrayList<FitDataMessageField> fields;
            private HashMap<Integer, FitDataMessageField> mapFields;
            private Long lastDateRead = 0L;
            public FitDataMessage(final Integer globalMessageNumber,
                                  final Integer localMessageNumber,
                                  final Integer numberOfFields) {
                this.globalMessageNumber = globalMessageNumber;
                this.localMessageNumber = localMessageNumber;
                this.fields = new ArrayList<>(numberOfFields);
                this.mapFields = new HashMap<>(numberOfFields);
            }
            public Collection<FitDataMessageField> getFields() {
                return mapFields.values();
            }
            public void addField(final FitDataMessageField field) {
                fields.add(field);
                mapFields.put(field.getFieldNumber(), field);
            }
            public FitDataMessageField getField(final Integer fieldNumber) {
                return mapFields.get(fieldNumber);
            }
            public Integer read(final InputStream inStream, final Boolean compressed, final Crc crc) throws IOException {
                Integer numberOfBytesRead = 0;
                for (FitDataMessageField field : fields) {
                    if (field.getFieldNumber().equals(TIMESTAMP_FIELD)) {
                        if (compressed) {
                            field.injectLong(lastDateRead);
                            System.out.println("Compressed header detected. Field #253 not read but set to last timestamp read.");
                            continue;
                        }
                    }
                    numberOfBytesRead += field.read(inStream, crc);
                    if (field.getFieldNumber().equals(TIMESTAMP_FIELD)) {
                        lastDateRead = field.asLong();
                    }
                }
                return numberOfBytesRead;
            }
            public String toString() {
                final StringBuilder result = new StringBuilder(1024);
                result.append("FitMessage[");
                result.append(globalMessageNumber);
                result.append("][");
                result.append(localMessageNumber);
                result.append("]=\n");
                for (FitDataMessageField field : fields) {
                    if (field.isInvalid() == null || field.isInvalid()) {
                        result.append("  ! ");
                    } else {
                        result.append("    ");
                    }
                    result.append(field.getFieldNumber());
                    result.append("=");
                    result.append(field.asString());
                    result.append("\n");
                }
                return result.toString();
            }
        }

        /**
         * Class for all kinds of data message field varieties.
         * Data message fields can have different content with different kind of endian.
         * The {@link #content} contains the real data.
         */
        static class FitDataMessageField {
            private FitDataMessageFieldType content;
            private Boolean isArray;
            private Integer sizeInBytes;
            public FitDataMessageField(final Integer fieldNumber,
                                       final Integer sizeInBytes,
                                       final Boolean isLittleEndian,
                                       final FitBaseType baseType) {
                this.isArray = !baseType.size.equals(sizeInBytes);
                this.sizeInBytes = sizeInBytes;
                content = FitDataMessageFieldTypeFactory.getFieldType(fieldNumber, sizeInBytes, isLittleEndian, baseType);
            }
            public String asString() {
                return content.asString();
            }
            public Integer asInteger() {
                return content.asInteger();
            }
            public Long asLong() {
                return content.asLong();
            }
            public Iterator<String> iteratorString() {
                return content.iteratorString();
            }
            public Iterator<Long> iteratorLong() {
                return content.iteratorLong();
            }
            public Boolean isInvalid() {
                return content.isInvalid();
            }
            public Boolean isArray() {
                return isArray;
            }
            public Integer getFieldNumber() {
                return content.getFieldNumber();
            }
            public Integer getSizeInBytes() {
                return sizeInBytes;
            }
            public Integer read(final InputStream inStream, final Crc crc) throws IOException {
                content.read(inStream, crc);
                return sizeInBytes;
            }
            public void injectLong(final Long toInject) {
                content.addLong(toInject);
            }
        }

        /**
         * Base class for all kind of data message content.
         * Encapsulates the endian handling and the conversion of content to String.
         */
        static abstract class FitDataMessageFieldType {
            private Integer fieldNumber;
            private ArrayList<String> contentArrayAsString;
            private ArrayList<Long> contentArrayAsLong;
            private Boolean isInvalid;
            private Byte[] invalidValue;

            public abstract void read(final InputStream inStream, final Crc crc) throws IOException;

            public FitDataMessageFieldType(final Integer fieldNumber,
                                           final Integer numberOfElements,
                                           final Byte[] invalidValue) {
                this.fieldNumber = fieldNumber;
                this.contentArrayAsString = new ArrayList<>(numberOfElements);
                this.contentArrayAsLong = new ArrayList<>(numberOfElements);
                this.invalidValue = invalidValue;
            }
            protected void clearForRead() {
                contentArrayAsString.clear();
                contentArrayAsLong.clear();
                isInvalid = false;
            }
            protected void addString(final String content) {
                contentArrayAsString.add(content);
            }
            protected void addLong(final Long content) {
                contentArrayAsLong.add(content);
            }
            public Integer getFieldNumber() {
                return fieldNumber;
            }
            public Byte[] getInvalidValue() {
                return invalidValue;
            }
            public Boolean isInvalid() {
                return isInvalid;
            }
            protected void setInvalid(final Boolean isInvalid) {
                this.isInvalid = isInvalid;
            }
            public String asString() {
                if (1 == contentArrayAsString.size()) {
                    return contentArrayAsString.get(0);
                }
                return Arrays.toString(contentArrayAsString.toArray());
            }
            public Integer asInteger() {
                return contentArrayAsLong.get(0).intValue();
            }
            public Long asLong() {
                return contentArrayAsLong.get(0);
            }
            public Iterator<String> iteratorString() {
                return contentArrayAsString.iterator();
            }
            public Iterator<Long> iteratorLong() {
                return contentArrayAsLong.iterator();
            }
        }

        /**
         * Encapsulates the creation of the specific FitDataMessageFieldType instances.
         */
        static class FitDataMessageFieldTypeFactory {
            public static FitDataMessageFieldType getFieldType(final Integer fieldNumber,
                                                               final Integer sizeInBytes,
                                                               final Boolean isLittleEndian,
                                                               final FitBaseType baseTye) {
                FitDataMessageFieldType result = null;
                final Integer numberOfElements = sizeInBytes / baseTye.size;
                if (0 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeEnum(fieldNumber, numberOfElements, baseTye.invalidValue);
                }
                if (1 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeSigned8(fieldNumber, numberOfElements, baseTye.invalidValue);
                }
                if (2 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeUnsigned8(fieldNumber, numberOfElements, baseTye.invalidValue);
                }
                if (3 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeSigned16(fieldNumber, numberOfElements, isLittleEndian, baseTye.invalidValue);
                }
                if (4 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeUnsigned16(fieldNumber, numberOfElements, isLittleEndian, baseTye.invalidValue);
                }
                if (5 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeSigned32(fieldNumber, numberOfElements, isLittleEndian, baseTye.invalidValue);
                }
                if (6 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeUnsigned32(fieldNumber, numberOfElements, isLittleEndian, baseTye.invalidValue);
                }
                if (7 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeString(fieldNumber, numberOfElements, baseTye.invalidValue);
                }
                if (8 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeFloat32(fieldNumber, numberOfElements, isLittleEndian, baseTye.invalidValue);
                }
                if (9 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeFloat64(fieldNumber, numberOfElements, isLittleEndian, baseTye.invalidValue);
                }
                if (10 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeUnsigned8(fieldNumber, numberOfElements, baseTye.invalidValue);
                }
                if (11 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeUnsigned16(fieldNumber, numberOfElements, isLittleEndian, baseTye.invalidValue);
                }
                if (12 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeUnsigned32(fieldNumber, numberOfElements, isLittleEndian, baseTye.invalidValue);
                }
                if (13 == baseTye.typeNumber) {
                    result = new FitDataMessageFieldTypeUnsigned8(fieldNumber, numberOfElements, baseTye.invalidValue);
                }
                return result;
            }
        }
        static class FitDataMessageFieldTypeString extends FitDataMessageFieldType {
            final StringArray data;
            public FitDataMessageFieldTypeString(final Integer fieldNumber,
                                                 final Integer numberOfElements,
                                                 final Byte[] invalidValue) {
                super(fieldNumber, numberOfElements, invalidValue);
                data = new StringArray(numberOfElements);
            }
            @Override
            public void read(final InputStream inStream, final Crc crc) throws IOException {
                clearForRead();
                data.getByteBuffer().clear();
                data.read(inStream, crc);
                setInvalid(data.isInvalid(getInvalidValue()));
                this.addString(data.toString());
            }
        }
        static class FitDataMessageFieldTypeSigned8 extends FitDataMessageFieldType {
            final Signed8Array data;
            public FitDataMessageFieldTypeSigned8(final Integer fieldNumber,
                                                  final Integer numberOfElements,
                                                  final Byte[] invalidValue) {
                super(fieldNumber, numberOfElements, invalidValue);
                data = new Signed8Array(numberOfElements);
            }
            @Override
            public void read(final InputStream inStream, final Crc crc) throws IOException {
                clearForRead();
                data.getByteBuffer().clear();
                data.read(inStream, crc);
                setInvalid(data.isInvalid(getInvalidValue()));
                for (Struct.Signed8 content : data.data) {
                    this.addLong((long) content.get());
                    this.addString(content.toString());
                }
            }
        }
        static class FitDataMessageFieldTypeUnsigned8 extends FitDataMessageFieldType {
            final Unsigned8Array data;
            public FitDataMessageFieldTypeUnsigned8(final Integer fieldNumber,
                                                    final Integer numberOfElements,
                                                    final Byte[] invalidValue) {
                super(fieldNumber, numberOfElements, invalidValue);
                data = new Unsigned8Array(numberOfElements);
            }
            @Override
            public void read(final InputStream inStream, final Crc crc) throws IOException {
                clearForRead();
                data.getByteBuffer().clear();
                data.read(inStream, crc);
                setInvalid(data.isInvalid(getInvalidValue()));
                for (Struct.Unsigned8 content : data.data) {
                    this.addLong((long) content.get());
                    this.addString(content.toString());
                }
            }
        }
        static class FitDataMessageFieldTypeEnum extends FitDataMessageFieldTypeUnsigned8 {
            public FitDataMessageFieldTypeEnum(Integer fieldNumber, Integer numberOfElements, Byte[] invalidValue) {
                super(fieldNumber, numberOfElements, invalidValue);
            }
        }
        static class FitDataMessageFieldTypeSigned16 extends FitDataMessageFieldType {
            final Signed16Array data;
            public FitDataMessageFieldTypeSigned16(final Integer fieldNumber,
                                                   final Integer numberOfElements,
                                                   final Boolean isLittleEndian,
                                                   final Byte[] invalidValue) {
                super(fieldNumber, numberOfElements, invalidValue);
                data = new Signed16Array(numberOfElements, isLittleEndian);
            }
            @Override
            public void read(final InputStream inStream, final Crc crc) throws IOException {
                clearForRead();
                data.getByteBuffer().clear();
                data.read(inStream, crc);
                setInvalid(data.isInvalid(getInvalidValue()));
                for (Struct.Signed16 content : data.data) {
                    this.addLong((long) content.get());
                    this.addString(content.toString());
                }
            }
        }
        static class FitDataMessageFieldTypeUnsigned16 extends FitDataMessageFieldType {
            final Unsigned16Array data;
            public FitDataMessageFieldTypeUnsigned16(final Integer fieldNumber,
                                                     final Integer numberOfElements,
                                                     final Boolean isLittleEndian,
                                                     final Byte[] invalidValue) {
                super(fieldNumber, numberOfElements, invalidValue);
                data = new Unsigned16Array(numberOfElements, isLittleEndian);
            }
            @Override
            public void read(final InputStream inStream, final Crc crc) throws IOException {
                clearForRead();
                data.getByteBuffer().clear();
                data.read(inStream, crc);
                setInvalid(data.isInvalid(getInvalidValue()));
                for (Struct.Unsigned16 content : data.data) {
                    this.addLong((long) content.get());
                    this.addString(content.toString());
                }
            }
        }
        static class FitDataMessageFieldTypeSigned32 extends FitDataMessageFieldType {
            final Signed32Array data;
            public FitDataMessageFieldTypeSigned32(final Integer fieldNumber,
                                                   final Integer numberOfElements,
                                                   final Boolean isLittleEndian,
                                                   final Byte[] invalidValue) {
                super(fieldNumber, numberOfElements, invalidValue);
                data = new Signed32Array(numberOfElements, isLittleEndian);
            }
            @Override
            public void read(final InputStream inStream, final Crc crc) throws IOException {
                clearForRead();
                data.getByteBuffer().clear();
                data.read(inStream, crc);
                setInvalid(data.isInvalid(getInvalidValue()));
                for (Struct.Signed32 content : data.data) {
                    this.addLong((long) content.get());
                    this.addString(content.toString());
                }
            }
        }
        static class FitDataMessageFieldTypeUnsigned32 extends FitDataMessageFieldType {
            final Unsigned32Array data;
            public FitDataMessageFieldTypeUnsigned32(final Integer fieldNumber,
                                                     final Integer numberOfElements,
                                                     final Boolean isLittleEndian,
                                                     final Byte[] invalidValue) {
                super(fieldNumber, numberOfElements, invalidValue);
                data = new Unsigned32Array(numberOfElements, isLittleEndian);
            }
            @Override
            public void read(final InputStream inStream, final Crc crc) throws IOException {
                clearForRead();
                data.getByteBuffer().clear();
                data.read(inStream, crc);
                setInvalid(data.isInvalid(getInvalidValue()));
                for (Struct.Unsigned32 content : data.data) {
                    this.addLong(content.get());
                    this.addString(content.toString());
                }
            }
        }
        static class FitDataMessageFieldTypeFloat32 extends FitDataMessageFieldType {
            final Float32Array data;
            public FitDataMessageFieldTypeFloat32(final Integer fieldNumber,
                                                  final Integer numberOfElements,
                                                  final Boolean isLittleEndian,
                                                  final Byte[] invalidValue) {
                super(fieldNumber, numberOfElements, invalidValue);
                data = new Float32Array(numberOfElements, isLittleEndian);
            }
            @Override
            public void read(final InputStream inStream, final Crc crc) throws IOException {
                clearForRead();
                data.getByteBuffer().clear();
                data.read(inStream, crc);
                setInvalid(data.isInvalid(getInvalidValue()));
                for (Struct.Float32 content : data.data) {
                    //this.addLong(Float.valueOf(content.get()).longValue());
                    this.addString(content.toString());
                    // Maybe we should provide the float value as float too?
                }
            }
        }
        static class FitDataMessageFieldTypeFloat64 extends FitDataMessageFieldType {
            final Float64Array data;
            public FitDataMessageFieldTypeFloat64(final Integer fieldNumber,
                                                  final Integer numberOfElements,
                                                  final Boolean isLittleEndian,
                                                  final Byte[] invalidValue) {
                super(fieldNumber, numberOfElements, invalidValue);
                data = new Float64Array(numberOfElements, isLittleEndian);
            }
            @Override
            public void read(final InputStream inStream, final Crc crc) throws IOException {
                clearForRead();
                data.getByteBuffer().clear();
                data.read(inStream, crc);
                setInvalid(data.isInvalid(getInvalidValue()));
                for (Struct.Float64 content : data.data) {
                    //this.addLong(Float.valueOf(content.get()).longValue());
                    this.addString(content.toString());
                    // Maybe we should provide the float value as float too?
                }
            }
        }
        /**
         * All fixed globale header structs are little endian and packed.
         * Only the specific structs can be big endian.
         */
        static class FitStructPacked extends Struct {
            private Boolean isLittleEndian = true;
            public FitStructPacked() {}
            public FitStructPacked(final Boolean isLittleEndian) {
                this.isLittleEndian = isLittleEndian;
            }
            public void setEndian(final Boolean isLittleEndian) {
                this.isLittleEndian = isLittleEndian;
            }
            @Override
            public ByteOrder byteOrder() {
                if (isLittleEndian) {
                    return ByteOrder.LITTLE_ENDIAN;
                } else {
                    return ByteOrder.BIG_ENDIAN;
                }
            }
            @Override
            public boolean isPacked() {
                return true;
            }
            public int read(final InputStream in, final Crc crc) throws IOException {
                int ret = super.read(in);
                crc.calculate(getByteBuffer());
                return ret;
            }
        }

        /**
         * ALL data types modelled as arrays except Strings.
         */
        static class StringArray extends FitStructPacked {
            public final UTF8String data;
            public StringArray(final Integer numberOfElements) {
                data = new UTF8String(numberOfElements);
            }
            public Boolean isInvalid(final Byte[] invalidValue) {
                if (data.struct().getByteBuffer().get(0) != invalidValue[0]) {
                    return false;
                }
                return true;
            }
        }
        static class Unsigned8Array extends FitStructPacked {
            public final Unsigned8[] data;
            public Unsigned8Array(final Integer numberOfElements) {
                data = array(new Unsigned8[numberOfElements]);
            }
            public Boolean isInvalid(final Byte[] invalidValue) {
                for (Unsigned8 datum : data) {
                    for (int i = 0; i < invalidValue.length; ++i) {
                        if (datum.struct().getByteBuffer().get(i) != invalidValue[i]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        static class Signed8Array extends FitStructPacked {
            public final Signed8[] data;
            public Signed8Array(final Integer numberOfElements) {
                data = array(new Signed8[numberOfElements]);
            }
            public Boolean isInvalid(final Byte[] invalidValue) {
                for (Signed8 datum : data) {
                    for (int i = 0; i < invalidValue.length; ++i) {
                        if (datum.struct().getByteBuffer().get(i) != invalidValue[i]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        static class Unsigned16Array extends FitStructPacked {
            public final Unsigned16[] data;
            public Unsigned16Array(final Integer numberOfElements, final Boolean isLittleEndian) {
                super(isLittleEndian);
                data = array(new Unsigned16[numberOfElements]);
            }
            public Boolean isInvalid(final Byte[] invalidValue) {
                for (Unsigned16 datum : data) {
                    for (int i = 0; i < invalidValue.length; ++i) {
                        if (datum.struct().getByteBuffer().get(i) != invalidValue[i]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        static class Signed16Array extends FitStructPacked {
            public final Signed16[] data;
            public Signed16Array(final Integer numberOfElements, final Boolean isLittleEndian) {
                super(isLittleEndian);
                data = array(new Signed16[numberOfElements]);
            }
            public Boolean isInvalid(final Byte[] invalidValue) {
                for (Signed16 datum : data) {
                    for (int i = 0; i < invalidValue.length; ++i) {
                        if (datum.struct().getByteBuffer().get(i) != invalidValue[i]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        static class Unsigned32Array extends FitStructPacked {
            public final Unsigned32[] data;
            public Unsigned32Array(final Integer numberOfElements, final Boolean isLittleEndian) {
                super(isLittleEndian);
                data = array(new Unsigned32[numberOfElements]);
            }
            public Boolean isInvalid(final Byte[] invalidValue) {
                for (Unsigned32 datum : data) {
                    for (int i = 0; i < invalidValue.length; ++i) {
                        if (datum.struct().getByteBuffer().get(i) != invalidValue[i]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        static class Signed32Array extends FitStructPacked {
            public final Signed32[] data;
            public Signed32Array(final Integer numberOfElements, final Boolean isLittleEndian) {
                super(isLittleEndian);
                data = array(new Signed32[numberOfElements]);
            }
            public Boolean isInvalid(final Byte[] invalidValue) {
                for (Signed32 datum : data) {
                    for (int i = 0; i < invalidValue.length; ++i) {
                        if (datum.struct().getByteBuffer().get(i) != invalidValue[i]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        static class Float32Array extends FitStructPacked {
            public final Float32[] data;
            public Float32Array(final Integer numberOfElements, final Boolean isLittleEndian) {
                super(isLittleEndian);
                data = array(new Float32[numberOfElements]);
            }
            public Boolean isInvalid(final Byte[] invalidValue) {
                for (Float32 datum : data) {
                    for (int i = 0; i < invalidValue.length; ++i) {
                        if (datum.struct().getByteBuffer().get(i) != invalidValue[i]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        static class Float64Array extends FitStructPacked {
            public final Float64[] data;
            public Float64Array(final Integer numberOfElements, final Boolean isLittleEndian) {
                super(isLittleEndian);
                data = array(new Float64[numberOfElements]);
            }
            public Boolean isInvalid(final Byte[] invalidValue) {
                for (Float64 datum : data) {
                    for (int i = 0; i < invalidValue.length; ++i) {
                        if (datum.struct().getByteBuffer().get(i) != invalidValue[i]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }

        /**
         * Encapsulates the basic data types as defined by the FIT standard.
         */
        static class FitBaseType {
            public String typeName;
            public Integer typeNumber;
            public Boolean endianAbility = false;
            public Byte[] invalidValue;
            public Integer size;
            public FitBaseType(final Integer typeNumber,
                               final String typeName,
                               final Boolean endianAbility,
                               final Byte[] invalidValue,
                               final Integer size) {
                this.typeNumber = typeNumber;
                this.typeName = typeName;
                this.endianAbility = endianAbility;
                this.invalidValue = invalidValue;
                this.size = size;
            }
        }

        /**
         * These are the basic data types as defined by the FIT standard.
         */
        static class FitDataTypes {
            private static final HashMap<Integer, FitBaseType> fitBaseTypeViaNumbers = new HashMap<>(16);

            static {
                new FitDataTypes();
            }

            private FitDataTypes() {
                FitBaseType baseType = new FitBaseType(0, "enum", false, new Byte[]{(byte) 0xFF}, 1);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(1, "sint8", false, new Byte[]{(byte) 0x7F}, 1);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(2, "uint8", false, new Byte[]{(byte) 0xFF}, 1);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(3, "sint16", true, new Byte[]{(byte) 0x7F, (byte) 0xFF}, 2);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(4, "uint16", true, new Byte[]{(byte) 0xFF, (byte) 0xFF}, 2);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(5, "sint32", true, new Byte[]{(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, 4);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(6, "uint32", true, new Byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, 4);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(7, "string", false, new Byte[]{(byte) 0x00}, 1);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(8, "float32", true, new Byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, 4);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(9, "float64", true, new Byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, 8);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(10, "uint8z", false, new Byte[]{(byte) 0x00}, 1);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(11, "uint16z", true, new Byte[]{(byte) 0x00, (byte) 0x00}, 2);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(12, "uint32z", true, new Byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}, 4);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
                baseType = new FitBaseType(13, "byte", false, new Byte[]{(byte) 0xFF}, 1);
                fitBaseTypeViaNumbers.put(baseType.typeNumber, baseType);
            }
            public static FitBaseType getBaseType(final Integer typeNumber) {
                return fitBaseTypeViaNumbers.get(typeNumber);
            }
        }

        /**
         * Record header structs.
         */
        static class FitByte extends FitStructPacked {
            public final Unsigned8 fitByte = new Unsigned8();
        }

        static class FitCrc extends FitStructPacked {
            public final Unsigned16 fitCrc = new Unsigned16();
            public FitCrc() {
                setEndian(true);
            }
        }

        static class FitHeader extends FitStructPacked {
            public final Unsigned8 headerSize = new Unsigned8();
            public final Unsigned8 protocolVersion = new Unsigned8();
            public final Unsigned16 profileVersion = new Unsigned16();
            public final Unsigned32 dataSize = new Unsigned32();
            public final Unsigned32 dataType = new Unsigned32();
        }

        static class FitRecordHeader extends FitStructPacked {
            public final BitField localMessageType = new BitField(4);
            public final BitField reserved2 = new BitField(1);
            public final BitField reserved1 = new BitField(1);
            public final BitField messageType = new BitField(1);
            public final BitField normal = new BitField(1);
        }

        static class FitCompressedHeader extends FitStructPacked {
            public final BitField timeOffset = new BitField(5);
            public final BitField localMessageType = new BitField(2);
            public final BitField normal = new BitField(1);
        }

        /**
         * Definition message fixed content structs.
         */
        static class FitDefinitionMessageContentFixed extends FitStructPacked {
            public final Unsigned16 globalMsgNum = new Unsigned16();
            public final Unsigned8 numberOfFields = new Unsigned8();
            public FitDefinitionMessageContentFixed(final Boolean isLittleEndian) {
                super(isLittleEndian);
            }
        }

        /**
         * Definition message field structs (aka. variable content structs).
         */
        static class FitDefinitionField extends FitStructPacked {
            public final Unsigned8 fieldDefinitionNumber = new Unsigned8();
            public final Unsigned8 size = new Unsigned8();
            public FitDefinitionField(final Boolean isLittleEndian) {
                super(isLittleEndian);
            }
        }

        /**
         * Definition message base type structs.
         */
        static class FitDefinitionFieldBaseType extends FitStructPacked {
            public final BitField baseTypeNumber = new BitField(5);
            public final BitField reserved = new BitField(2);
            public final BitField endianess = new BitField(1);
            public FitDefinitionFieldBaseType(final Boolean isLittleEndian) {
                super(isLittleEndian);
            }
        }

        /**
         * Contains the information, we use in GPSMaster for {@link Waypoint} content.
         */
        static class FitRecord {
            public Double posLat;
            public Double posLong;
            public Double altitude;
            public Integer cadence;
            public Integer heartrate;
            public Integer temperature;
            public Integer zone;
            public Integer enhancedSpeed;
            public DateTime timestamp;
            public Double hrv;
            public String toString() {
                String content = "FitRecord:" +
                        "\n  posLat=" + posLat +
                        "\n  posLong=" + posLong +
                        "\n  altitude=" + altitude +
                        "\n  cadence=" + cadence +
                        "\n  heartrate=" + heartrate +
                        "\n  temperature=" + temperature +
                        "\n  zone=" + zone +
                        "\n  speed=" + enhancedSpeed +
                        "\n  hrv=" + hrv +
                        "\n  time=" + timestamp;
                return content;
            }
        }

        /**
         * FIT stores timestamps as long values with seconds since 31.12.1989 00:00:000.000.
         *
         * @param fitTimestamp The value to convert
         * @return The converted timestamp
         */
        public DateTime convertToJodaTime(final Integer fitTimestamp) {
            final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.set(Calendar.DAY_OF_MONTH, 31);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.YEAR, 1989);
 //           System.err.println("***Time before add=" + cal.getTime().toString());
            cal.add(Calendar.SECOND, fitTimestamp);
 //           System.err.println("***Time after add=" + cal.getTime().toString());
            return new DateTime(cal.getTime(), DateTimeZone.UTC);
        }

        /**
         * To store the definition message objects read from the file.
         */
        private HashMap<Integer, FitDataMessage> messageDefinitions = new HashMap<>(16);

        /**
         * Validation means only to check the header for the ".FIT" magic and to check the crc.
         * @param inStream The FIT file to validate
         * @throws IOException
         * @throws ValidationException
         */
        public void validateFitFile(final InputStream inStream) throws IOException, ValidationException {
            final Crc crc = new Crc();
            final FitHeader header = new FitHeader();
            header.read(inStream, crc);
            //(".FIT");
            if (header.dataType.get() != 0x5449462E) {
                throw new ValidationException("File is not a FIT file!");
            }
            Long bytesToRead = header.dataSize.get();
            if (14 == header.headerSize.get()) {
                long headerCrc = crc.getValue();
                final FitCrc crcHeader = new FitCrc();
                crcHeader.read(inStream, crc);
                if (crcHeader.fitCrc.get() != 0 && crcHeader.fitCrc.get() != headerCrc) {
                    throw new ValidationException("FIT header CRC mismatch. Expected:" + Integer.toHexString(crcHeader.fitCrc.get())
                            + " but calculated:" + Long.toHexString(crc.getValue()));
                }
            }
            final byte[] buffer = new byte[bytesToRead.intValue()];
            int read = inStream.read(buffer);
            if (read != bytesToRead.intValue()) {
                throw new ValidationException("Failed to read all data. Read:" + read + " but expected:" + bytesToRead);
            }
            crc.calculate(buffer);
            FitCrc crcFile = new FitCrc();
            crcFile.read(inStream);
            if (crc.getValue() == 0 || crcFile.fitCrc.get() != crc.getValue()) {
                throw new ValidationException("FIT file CRC mismatch. Expected:" + Integer.toHexString(crcFile.fitCrc.get())
                        + " but calculated:" + Long.toHexString(crc.getValue()));
            }
        }

        /**
         * Reads the FIT file into the {@link FitRecord}s.
         * Does not handle multiple files within one file!!! The FIT specification would allow something like that.
         *
         * @param inStream The FIT file.
         * @throws IOException
         * @throws InstantiationException
         * @throws IllegalAccessException
         * @throws NoSuchMethodException
         * @throws InvocationTargetException
         */
        public void parseFitFile(final InputStream inStream)
                throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ValidationException {
            // We start at lap 0
            lap = 0;
            laps.clear();
            records.clear();
            final Crc crc = new Crc();
            final FitHeader header = new FitHeader();
            header.read(inStream, crc);
            final Boolean isFit = (header.dataType.get() == 0x5449462E); //(".FIT");
            final Long bytesToRead = header.dataSize.get();
            System.err.println("  Bytes to read: " + bytesToRead);
//            System.err.println("header size:" + header.headerSize.toString());
//            System.err.println("data size:" + header.dataSize.toString());
//            System.err.println("data type:" + header.dataType.toString());
//            System.err.println("header:" + header.toString());
            if (14 == header.headerSize.get()) {
                long headerCrc = crc.getValue();
                final FitCrc crcHeader = new FitCrc();
                crcHeader.read(inStream, crc);
                if (crcHeader.fitCrc.get() != 0 && crcHeader.fitCrc.get() != headerCrc) {
                    throw new ValidationException("FIT header CRC mismatch. Expected:" + Integer.toHexString(crcHeader.fitCrc.get())
                            + " but calculated:" + Long.toHexString(headerCrc));
                }
            }
            if (!isFit) {
                System.err.println("File is not a FIT file!");
                throw new ValidationException("File is not a FIT file!");
            }

            long bytesRead = 0;
            // A FIT file starts with messages right behind the header.
            final FitRecordHeader messageHeader = new FitRecordHeader();
            final FitCompressedHeader compressedHeader = new FitCompressedHeader();
            Integer localMessageType = -1;
            Boolean compressed = false;
            final FitDefinitionMessageContentFixed definitionHeader = new FitDefinitionMessageContentFixed(true);
            final FitDefinitionField definitionField = new FitDefinitionField(true);
            final FitDefinitionFieldBaseType definitionBaseType = new FitDefinitionFieldBaseType(true);
            final FitByte reserved = new FitByte();
            final FitByte endianess = new FitByte();

            while (0 != inStream.available() && bytesRead < bytesToRead) {
                messageHeader.read(inStream, crc);
                bytesRead += messageHeader.size();
                // Regular data message/definition message (0) or compressed data message (1)?
                if (0 == messageHeader.normal.shortValue()) {
                    compressed = false;
                    localMessageType = messageHeader.localMessageType.intValue();
                    if (1 == messageHeader.messageType.shortValue()) {
//                        System.err.println(" record is definition message");
                        bytesRead += reserved.read(inStream, crc);
                        bytesRead += endianess.read(inStream, crc);
                        final Boolean isLittleEndian = (0 == endianess.fitByte.get());
                        definitionHeader.setEndian(isLittleEndian);
                        bytesRead += definitionHeader.read(inStream, crc);
                        final FitDataMessage dataMessage = new FitDataMessage(definitionHeader.globalMsgNum.get(),
                                messageHeader.localMessageType.intValue(),
                                Integer.valueOf(definitionHeader.numberOfFields.get()));
                        messageDefinitions.put(dataMessage.localMessageNumber, dataMessage);
                        for (int i = 0; i < definitionHeader.numberOfFields.get(); ++i) {
                            definitionField.setEndian(isLittleEndian);
                            bytesRead += definitionField.read(inStream, crc);
                            definitionBaseType.setEndian(isLittleEndian);
                            bytesRead += definitionBaseType.read(inStream, crc);
                            FitBaseType bt = FitDataTypes.getBaseType(definitionBaseType.baseTypeNumber.intValue());
                            final FitDataMessageField dataField = new FitDataMessageField(Integer.valueOf(definitionField.fieldDefinitionNumber.get()),
                                    Integer.valueOf(definitionField.size.get()),
                                    isLittleEndian,
                                    bt);
                            dataMessage.addField(dataField);
                        }
                        // Processing complete -> next message
                        continue;
                    }
                } else {
                    compressed = true;
                    compressedHeader.setByteBuffer(messageHeader.getByteBuffer(), 0);
                    if (1 != compressedHeader.normal.shortValue()) {
                        System.err.println("Failed to set compressed header content");
                        throw new ValidationException("Failed to set compressed header content");
                    }
                    localMessageType = compressedHeader.localMessageType.intValue();
//                    System.err.println("record.normal:" + compressedHeader.normal.toString());
//                    System.err.println("record.localMessageType:" + compressedHeader.localMessageType.toString());
//                    System.err.println("record.timeOffset:" + compressedHeader.timeOffset.toString());
//                    System.err.println("recordHeader:" + compressedHeader.toString());
                }
                FitDataMessage dataMessage = messageDefinitions.get(localMessageType);
                if (null == dataMessage) {
                    System.err.println("Invalid FIT structure. Data message references unknown definition message.");
                    throw new ValidationException("Invalid FIT structure. Data message references unknown definition message.");
                }
                bytesRead += dataMessage.read(inStream, compressed, crc);
//                System.err.println(dataMessage.toString());
                switch (dataMessage.globalMessageNumber) {
                    case FILEID_MSG: {
                        FitDataMessageField field = dataMessage.getField(FILEID_FIELD_TYPE);
                        if (null != field) {
                            if (!field.asString().equals(FILEID_TYPE_ACTIVITY.toString())) {
                                System.err.println("FIT file is not an activity file. Unable to proceed!");
                                records.clear();
                                throw new ValidationException("FIT file is not an activity file. Unable to proceed!");
                            } else {
                                System.out.println("FIT activity file detected!");
                            }
                        }
                        field = dataMessage.getField(FILEID_FIELD_CREATETIME);
                        if (null != field) {
                            DateTime dateTime = convertToJodaTime(field.asInteger());
                            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss Z");
                            name = fmt.print(dateTime);
                        }
                        field = dataMessage.getField(FILEID_FIELD_SERIAL);
                        if (null != field && !field.isInvalid()) {
                            creator = field.asString();
                        }
                    }
                    break;
                    case HRV_MSG: {
                        FitDataMessageField field = dataMessage.getField(HRV_FIELD_TIME);
                        if (null != field) {
                            Integer raw = field.asInteger();
                            Double hrv = 1.0 * raw / HRV_TIME_SCALE;
                            lastHrv = hrv;
                        }
                    }
                    break;
                    case RECORD_MSG: {
                        final FitRecord record = new FitRecord();
                        record.hrv = lastHrv;
                        FitDataMessageField field = dataMessage.getField(RECORD_FIELD_POS_LAT);
                        if (null != field) {
                            Integer raw = field.asInteger();
                            Double latitude = convertSemicirclesToDegree(raw);
                            record.posLat = latitude;
                        }
                        field = dataMessage.getField(RECORD_FIELD_POS_LONG);
                        if (null != field) {
                            Integer raw = field.asInteger();
                            Double longitude = convertSemicirclesToDegree(raw);
                            record.posLong = longitude;
                        }
                        field = dataMessage.getField(RECORD_FIELD_ALTITUDE);
                        if (null != field) {
                            Double alt = field.asLong().doubleValue();
                            alt = alt / RECORD_ALTITUDE_SCALE;
                            alt += RECORD_ALTITUDE_OFFSET;
                            record.altitude = alt;
                        }
                        field = dataMessage.getField(RECORD_FIELD_TIMESTAMP);
                        if (null != field) {
                            Integer raw = field.asInteger();
                            DateTime dt = convertToJodaTime(raw);
                            record.timestamp = dt;
                        }
                        field = dataMessage.getField(RECORD_FIELD_CADENCE);
                        if (null != field) {
                            record.cadence = field.asInteger();
                        }
                        field = dataMessage.getField(RECORD_FIELD_HEARTRATE);
                        if (null != field) {
                            record.heartrate = field.asInteger();
                        }
                        field = dataMessage.getField(RECORD_FIELD_TEMPERATURE);
                        if (null != field) {
                            record.temperature = field.asInteger();
                        }
                        field = dataMessage.getField(RECORD_FIELD_ENHANCED_SPEED);
                        if (null != field) {
                            Integer speed = field.asInteger();
                            speed = speed / RECORD_ENHANCED_SPEED_SCALE;
                            record.enhancedSpeed = speed;
                        }
                        field = dataMessage.getField(RECORD_FIELD_ZONE);
                        if (null != field) {
                            record.zone = field.asInteger();
                        }
                        records.add(record);
//                        System.err.println(record.toString());
                    }
                    break;
                    case LAP_MSG: {
                        FitDataMessageField field = dataMessage.getField(LAP_FIELD_TIMESTAMP);
                        if (null != field) {
                            laps.add(new ArrayList<>(records));
                            records.clear();
                            ++lap;
                        }
                    }
                    break;
                    case CREATOR_MSG: {
                        FitDataMessageField field = dataMessage.getField(CREATOR_FIELD_SVERSION);
                        if (null != field && !field.isInvalid()) {
                            creator += "." + field.asString();
                        }
                        field = dataMessage.getField(CREATOR_FIELD_HVERSION);
                        if (null != field && !field.isInvalid()) {
                            creator += "." + field.asString();
                        }
                    }
                }
            }
            // Collected records since last lap change? -> store
            if (!records.isEmpty()) {
                laps.add(records);
            }
            System.err.println("  Bytes read: " + bytesRead);
            if (bytesRead != bytesToRead) {
                throw new ValidationException("Had to read " + bytesToRead + " bytes, but read only " + bytesRead + ". Can not proceed.");
            }
            if (inStream.available() > 0) {
                final FitCrc crcFile = new FitCrc();
                crcFile.read(inStream);
                if (crc.getValue() != crcFile.fitCrc.get()) {
                    throw new ValidationException("FIT file CRC mismatch. Expected:" + Integer.toHexString(crcFile.fitCrc.get())
                            + " but calculated:" + Long.toHexString(crc.getValue()));
                }
            }
        }

        /**
         * Generate the {@link Waypoint}s out of the {@link FitRecord}s.
         *
         * @param lapRecords The ordered list of records to convert to waypoints
         * @param wptGroup The group to add the waypoints to
         * @param gpx The {@link GPXFile} to add the extension praefix to
         */
        public void generateWaypoints(ArrayList<FitRecord> lapRecords, final WaypointGroup wptGroup, final GPXFile gpx) {
            for (final FitRecord record : lapRecords) {
                if (record.posLat == null || record.posLong == null) {
                    System.err.println("Record with empty latitude or empty longitude ignored!");
                    continue;
                }
                final Waypoint wpt = new Waypoint(record.posLat, record.posLong);
                if (null != record.timestamp) {
                    wpt.setTime(record.timestamp.toDate());
                }
                if (null != record.altitude) {
                    wpt.setEle(record.altitude);
                }
                // Now some stuff for the extensions
                if (null != record.cadence) {
                    if (!gpx.getExtensionPrefixes().contains("tp1")) {
                        gpx.addExtensionPrefix("tp1");
                    }
                    GPXExtension gpxExtension = wpt.getExtension().getExtension("tp1:TrackPointExtension");
                    if (null == gpxExtension) {
                        gpxExtension = new GPXExtension("tp1:TrackPointExtension");
                        gpxExtension.setNamespace("http://www.garmin.com/xmlschemas/TrackPointExtension/v1");
                        wpt.getExtension().add(gpxExtension);
                    }
                    gpxExtension.add("tp1:cad", record.cadence.toString());
                }
                if (null != record.heartrate) {
                    if (!gpx.getExtensionPrefixes().contains("tp1")) {
                        gpx.addExtensionPrefix("tp1");
                    }
                    GPXExtension gpxExtension = wpt.getExtension().getExtension("tp1:TrackPointExtension");
                    if (null == gpxExtension) {
                        gpxExtension = new GPXExtension("tp1:TrackPointExtension");
                        gpxExtension.setNamespace("http://www.garmin.com/xmlschemas/TrackPointExtension/v1");
                        wpt.getExtension().add(gpxExtension);
                    }
                    gpxExtension.add("tp1:hr", record.heartrate.toString());
                }
                if (null != record.temperature) {
                    if (!gpx.getExtensionPrefixes().contains("tp1")) {
                        gpx.addExtensionPrefix("tp1");
                    }
                    GPXExtension gpxExtension = wpt.getExtension().getExtension("tp1:TrackPointExtension");
                    if (null == gpxExtension) {
                        gpxExtension = new GPXExtension("tp1:TrackPointExtension");
                        gpxExtension.setNamespace("http://www.garmin.com/xmlschemas/TrackPointExtension/v1");
                        wpt.getExtension().add(gpxExtension);
                    }
                    gpxExtension.add("tp1:atemp", record.temperature.toString());
                }
                if (record.hrv != 0.0) {
                    GPXExtension gpxExtension = new GPXExtension("gpsm:HeartRateVariability", Double.toString(record.hrv));
                    wpt.getExtension().add(gpxExtension);
                }
                if (null != record.enhancedSpeed) {
                    GPXExtension gpxExtension = new GPXExtension("gpsm:Acceleration", record.enhancedSpeed.toString());
                    wpt.getExtension().add(gpxExtension);
                }
                if (null != record.zone) {
                    GPXExtension gpxExtension = new GPXExtension("gpsm:Zone", record.zone.toString());
                    wpt.getExtension().add(gpxExtension);
                }
                wptGroup.addWaypoint(wpt);
            }
        }
    }

    FitLoader() {
        super();
        isAdding = false;
        isDefault = false;
        extensions.add("fit");
    }

    /**
     * @param inStream
     * @return
     */
    @Override
    public GPXFile load(InputStream inStream, String format) throws Exception {
        gpx = new GPXFile();
        final FitParser reader = new FitParser();
        reader.parseFitFile(inStream);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
        final Integer laps = reader.getLaps();
        for (int i = 0; i < laps; ++i) {
            ArrayList<FitParser.FitRecord> lapRecords = reader.getLapRecords(i);
            if (null == lapRecords || lapRecords.isEmpty()) {
                continue;
            }
            Track track = new Track(gpx.getColor(currentLoadColor++));
            track.setName(fmt.print(lapRecords.get(0).timestamp));
            WaypointGroup waypointGroup = track.addTrackseg();
            reader.generateWaypoints(lapRecords, waypointGroup, gpx);
            if (waypointGroup.getNumPts() != 0) {
                gpx.addTrack(track);
            } else {
                throw new ValidationException("FIT file contained no usable track information!");
            }
        }
        gpx.setCreator(reader.getCreator());
        gpx.setName(reader.getName());
        return gpx;
    }

    /**
     * @param inStream
     * @throws Exception
     */
    @Override
    public void loadCumulative(InputStream inStream) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * @param gpx
     * @param file
     * @throws FileNotFoundException
     */
    @Override
    public void save(GPXFile gpx, File file) throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }

    /**
     * @param gpx
     * @param outStream
     */
    @Override
    public void save(GPXFile gpx, OutputStream outStream) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws ValidationException
     * @throws
     */
    @Override
    public void validate(InputStream inStream) throws ValidationException {
        try {
            final FitParser reader = new FitParser();
            reader.validateFitFile(inStream);
        } catch (Exception e) {
            System.err.println("Exception during validation.");
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new ValidationException(e.getMessage());
        } finally {
        }
    }

    /**
     *
     */
    @Override
    public void close() {
        if (bais != null) {
            try {
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bais = null;
        }
        this.file = null;
        isOpen = false;
    }

    /**
     * get if this loader can validate delivered data
     *
     * @return
     */
    @Override
    public boolean canValidate() {
        return true;
    }
}
