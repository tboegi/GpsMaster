package org.gpsmaster.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.gpsmaster.pathfinder.PathFinder;
import org.gpsmaster.pathfinder.RouteProvider;
import org.gpsmaster.pathfinder.Transport;

import eu.fuegenstein.swing.Widget;
import eu.fuegenstein.swing.WidgetLayout;

/**
 * Widget for the selection of RoutingProviders and RouteTypes
 * @author rfu
 *
 */
public class PathFinderWidget extends Widget {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1620520040789173557L;
	
	private final Border CELLBORDER = new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(2, 4, 2, 4));
	private final Color BACKGROUNDSELECTED = new Color(177, 177, 25, 128);
	private final Color LABELBACKGROUND = new Color(255, 255, 255, 0);
	private final Color CANCELBACKGROUND = new Color(177, 25, 25, 208);
	
	private PathFinder pathFinder = null;
	private JPanel providerPanel = null;
	private JPanel transportPanel = null;
	
	private List<ProviderLabel> providerLabels = new ArrayList<PathFinderWidget.ProviderLabel>();
	private List<TransportLabel> transportLabels = new ArrayList<PathFinderWidget.TransportLabel>();

	private JPanel cancelPanel = new JPanel();	
	private PropertyChangeListener cancelListener = null;

	/**
	 * helper class: JLabel to hold route provider
	 * 
	 * @author rfu
	 */
	private class ProviderLabel extends JLabel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 7696895198511994447L;
		private RouteProvider provider = null;
		/**
		 * @return the currentProvider
		 */
		public RouteProvider getRouteProvider() {
			return provider;
		}

		/**
		 * @param currentProvider the currentProvider to set
		 */
		public void setRouteProvider(RouteProvider routeProvider) {
			this.provider = routeProvider;
			this.setText(routeProvider.getName());
		}			
	}

	/**
	 * Helper class
	 * @author rfu
	 *
	 */
	private class TransportLabel  extends JLabel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2139638356031334870L;
		private Transport transport = null;

		/**
		 * @return the transport
		 */
		public Transport getTransport() {
			return transport;
		}

		/**
		 * @param transport the transport to set
		 */
		public void setTransport(Transport transport) {			
			this.transport = transport;
			this.setText(transport.getName());
			this.setToolTipText(transport.getDescription());
		}		
	}

	/**
	 * Constructor
	 * @param pathFinder
	 */
	public PathFinderWidget(final PathFinder pathFinder) {
		
		super(WidgetLayout.TOP_LEFT);
		
		this.pathFinder = pathFinder;		
		this.pathFinder.setWidget(this);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(BACKGROUNDCOLOR);
		setBorder(new EmptyBorder(2, 2, 2, 2));
		setOpaque(false);
		
		JPanel titlePanel = new JPanel();
		titlePanel.add(new JLabel("Select Route Provider"));		
		titlePanel.setAlignmentX(CENTER_ALIGNMENT);
		titlePanel.setBackground(BACKGROUNDCOLOR);
		add(titlePanel);
				
		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(BACKGROUNDCOLOR);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
		// centerPanel.setBorder(new EmptyBorder(0, 2, 0, 2));
		
		providerPanel = new JPanel();
		providerPanel.setLayout(new BoxLayout(providerPanel, BoxLayout.Y_AXIS));
		providerPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		// providerPanel.setOpaque(false);
		providerPanel.setBackground(BACKGROUNDCOLOR);
		providerPanel.setAlignmentY(TOP_ALIGNMENT);
		centerPanel.add(providerPanel);
		
		transportPanel = new JPanel();
		transportPanel.setLayout(new BoxLayout(transportPanel, BoxLayout.Y_AXIS));
		transportPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		transportPanel.setBackground(BACKGROUNDCOLOR);
		transportPanel.setAlignmentY(TOP_ALIGNMENT);
		
		centerPanel.add(transportPanel);
		add(centerPanel);
				
		cancelPanel.add(new JLabel("Cancel"));
		cancelPanel.setBackground(CANCELBACKGROUND);
		cancelPanel.setOpaque(true);
		cancelPanel.setVisible(false);
		cancelPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				pathFinder.Cancel();
			}
		});
		add(cancelPanel);
		
	}
	
	/**
	 * @return the pathFinder
	 */
	public PathFinder getPathFinder() {
		return pathFinder;
	}

	/**
	 * @param pathFinder the pathFinder to set
	 */
	public void setPathFinder(PathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}
	
	/**
	 * 
	 * @param providers
	 */
	public void setRouteProviders(List<RouteProvider> providers) {
		if (providers.size() == 0) {
			throw new IllegalArgumentException("empty provider list");
		}
		clearProviderPanel();
		makeProviderPanel(providers);
		setCurrentProvider(providerLabels.get(0));
		setCurrentTransport(transportLabels.get(0));
		
	}
	
	/**
	 * Set this widget in "Busy Mode": 
	 * disable interactive controls (TBI), * enable "Cancel" msgPanel 
	 * @param busy
	 */
	public void setBusy(boolean busy) {
		cancelPanel.setVisible(busy);
	}
	
	/**
	 * 
	 * @param transports
	 */
	private void makeTransportPanel(List<Transport> transports) {
		
		int maxWidth = 0;
		int maxHeight = 0;
		
		MouseListener transportClickListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				TransportLabel label = (TransportLabel) e.getSource();
				setCurrentTransport(label);
			}
		};
		
		for (Transport transport : transports) {
			TransportLabel label = new TransportLabel();
			label.setTransport(transport);
			label.addMouseListener(transportClickListener);
			label.setOpaque(true);
			label.setBorder(CELLBORDER);
			label.setBackground(LABELBACKGROUND);			
	        maxWidth = Math.max(maxWidth, label.getPreferredSize().width);
	        maxHeight = Math.max(maxHeight, label.getPreferredSize().height);
			
			transportLabels.add(label);
			transportPanel.add(label);
		}
        
		Dimension dim = new Dimension(maxWidth, maxHeight);
		for (TransportLabel label : transportLabels) {
			label.setPreferredSize(dim);
			label.setMaximumSize(dim);
			label.setMinimumSize(dim);
		}
		
		transportPanel.revalidate();
	}
	
	/**
	 * populate the providerPanel with labels for each provider
	 * @param providers
	 */
	private void makeProviderPanel(List<RouteProvider> providers) {

		int maxWidth = 0;
		int maxHeight = 0;
		
		// Listener for click on label
		MouseListener providerClickListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	ProviderLabel label = ((ProviderLabel) e.getSource()); 
            	setCurrentProvider(label); 
            	clearTransportPanel();
            	makeTransportPanel(label.getRouteProvider().getTransport());
            	setCurrentTransport(transportLabels.get(0));
            }		
		};
		
		for (RouteProvider provider : providers) {
			ProviderLabel label = new ProviderLabel();
			label.setRouteProvider(provider);
			label.setToolTipText(provider.getDescription());
			label.addMouseListener(providerClickListener);
			label.setOpaque(true);
			label.setBorder(CELLBORDER);
			label.setBackground(LABELBACKGROUND);
	        maxWidth = Math.max(maxWidth, label.getPreferredSize().width);
	        maxHeight = Math.max(maxHeight, label.getPreferredSize().height);
			
			providerLabels.add(label);
			providerPanel.add(label);			
		}
		Dimension dim = new Dimension(maxWidth, maxHeight);
		for (ProviderLabel label : providerLabels) {
			label.setPreferredSize(dim);
			label.setMaximumSize(dim);
			label.setMinimumSize(dim);
		}
		
		providerPanel.revalidate();
	}
		
	/**
	 * Highlight the selected route provider
	 * @param current
	 */
	private void setCurrentProvider(ProviderLabel current) {
		
		for (ProviderLabel label : providerLabels) {
			if (label.equals(current)) {
				System.out.println("current provider = " + current.getRouteProvider().getName());
				label.setBackground(BACKGROUNDSELECTED);
				pathFinder.setRouteProvider(label.getRouteProvider());
				
				// prepare & show transport labels
				clearTransportPanel();
				makeTransportPanel(label.getRouteProvider().getTransport());
				// setCurrentTransport(transportLabels.get(0));
			} else {
				label.setBackground(BACKGROUNDCOLOR);				
			}
		}
	}

	/**
	 * 
	 * @param current
	 */
	private void setCurrentTransport(TransportLabel current) {
		
		for (TransportLabel label : transportLabels) {
			if (label.equals(current)) {
				System.out.println("current transport = " + current.getTransport().getName());
				label.setBackground(BACKGROUNDSELECTED);
				pathFinder.getRouteProvider().setRouteType(label.getTransport());
			} else {
				label.setBackground(BACKGROUNDCOLOR);				
			}
		}
	}

	/**
	 * Remove all providers from the provider msgPanel 
	 */
	private void clearProviderPanel() {
		
		for (ProviderLabel label : providerLabels) {
			providerPanel.remove(label);
		}
		providerLabels.clear();
	}

	/**
	 * Remove all transports from the transport msgPanel
	 */
	private void clearTransportPanel() {
		for (TransportLabel label : transportLabels) {
			transportPanel.remove(label);
		}
		transportLabels.clear();
	}
}
