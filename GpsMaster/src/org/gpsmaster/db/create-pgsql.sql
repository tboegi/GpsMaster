-- Table: dat_gps
DROP TABLE dat_gps;

CREATE TABLE dat_gps
(
  id bigint NOT NULL,
  "name" character varying(255),
  color bigint DEFAULT 0,
  start_dt timestamp with time zone,
  end_dt timestamp with time zone,
  distance double precision NOT NULL DEFAULT 0,
  duration bigint,
  min_lat double precision NOT NULL DEFAULT 0,
  max_lat double precision NOT NULL DEFAULT 0,
  min_lon double precision NOT NULL DEFAULT 0,
  max_lon double precision NOT NULL DEFAULT 0,
  "activity" character varying(25),
  loader_class character varying(100),
  fileformat character varying(5),
  prog_version character varying(100),
  data bytea NOT NULL,
  source_urn character varying(255),
  user_id bigint NOT NULL DEFAULT 0,
  compressed boolean NOT NULL DEFAULT false,
  entry_dt timestamp with time zone NOT NULL DEFAULT now(),
  checksum character varying(32),
  CONSTRAINT pk_id PRIMARY KEY (id)
)
WITHOUT OIDS;
ALTER TABLE dat_gps OWNER TO gpsmaster;
