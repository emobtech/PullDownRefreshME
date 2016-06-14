/* PullDownRefresh.java
 * 
 * User Interface ME
 * Copyright (c) 2013 eMob Tech (http://www.emobtech.com/)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.emobtech.uime.lwuit;

import java.io.IOException;
import java.util.Timer;

import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.plaf.Style;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.EventDispatcher;

/**
 * <p>
 * A PullDownRefresh object provides a standard component that can be used to 
 * initiate the refreshing of a {@link Form}’s contents.
 * </p>
 * <p>
 * <b>Important:</b> PullDownRefresh must be added ONLY to a {@link Form} and 
 * at the FIRST position. 
 * </p>
 * <p>
 * It is possible to customize the style, images and labels of this component:
 * </p>
 * <p>
 * <b>Style:</b><br/><br/>To customize the style, do it via Resource Editor, 
 * through the UIIDs <b>PullDownRefreshContentPane</b> and 
 * <b>PullDownRefreshLabel</b>.
 * </p>
 * <p>
 * <b>Images:</b><br/><br/>To change the images, replace the files 
 * <b>"/PullDownRefresh/arrow.png"</b> and <b>"/PullDownRefresh/refresh.png"</b>
 * in the project's resource folder or enter new ones in the class constructor.
 * </p>
 * <p>
 * <b>Labels:</b><br/><br/>To set a new text for the labels, use the methods 
 * {@link PullDownRefresh#setStateDefaultLabel(String)}, 
 * {@link PullDownRefresh#setStatePullingLabel(String)} and 
 * {@link PullDownRefresh#setStateRefreshingLabel(String)}.
 * </p>
 * @author Ernandes Jr. (ernandes@emobtech.com)
 * @version 1.1
 */
public final class PullDownRefresh extends Container {
	/**
	 * <p>
	 * Initial Y position.
	 * </p>
	 */
	private int initialY = Integer.MIN_VALUE;

	/**
	 * <p>
	 * State of refreshing.
	 * </p>
	 */
	private boolean refreshing;
	
	/**
	 * <p>
	 * State of triggered.
	 * </p>
	 */
	private boolean triggered;
	
	/**
	 * <p>
	 * State indicator.
	 * </p>
	 */
	private PullDownRefreshIndicator indicator;
	
	/**
	 * <p>
	 * Label.
	 * </p>
	 */
	private Label label;
	
	/**
	 * <p>
	 * Event dispatcher.
	 * </p>
	 */
	private final EventDispatcher dispatcher = new EventDispatcher();
	
	/**
	 * <p>
	 * Label of default state.
	 * </p>
	 */
	private String stateDefaulLabel = "Pull down to refresh";
	
	/**
	 * <p>
	 * Label of "pulling" state.
	 * </p>
	 */
	private String statePullingLabel = "Release to refresh";
	
	/**
	 * <p>
	 * Label of "refreshing" state.
	 * </p>
	 */
	private String stateRefreshingLabel = "Refreshing...";
	
	/**
	 * Visible on default state.
	 */
	private boolean visibleOnDefaultState;
	
	/**
	 * <p>
	 * Pulling length.
	 * </p>
	 */
	private int pullingLength;
	
	/**
	 * <p>
	 * Creates a new instance of PullDownRefresh.
	 * </p>
	 */
	public PullDownRefresh() {
		this(true, (Image)null, (Image)null);
	}
	
	/**
	 * @param visibleOnDefaultState
	 * @param arrowName Arrow image constant name.
	 * @param refreshName Refresh image constant name.
	 */
	public PullDownRefresh(String arrowName, String refreshName) {
		this(
			true,
			UIManager.getInstance().getThemeImageConstant(arrowName),
			UIManager.getInstance().getThemeImageConstant(refreshName));
	}
	
	/**
	 * <p>
	 * Creates a new instance of PullDownRefresh.
	 * </p>
	 * @param visibleOnDefaultState Indicates whether the component must remain
	 *                              visible even on default state.
	 * @param arrow Arrow image. <code>null</code> loads default image.
	 * @param refresh Refresh image. <code>null</code> loads default image.
	 */
	public PullDownRefresh(boolean visibleOnDefaultState, Image arrow,
		Image refresh) {
		this.visibleOnDefaultState = visibleOnDefaultState;
		//
		setLayout(new BorderLayout());
		setUIID("PullDownRefreshContentPane");
		//
		label = new Label(stateDefaulLabel);
		label.setUIID("PullDownRefreshLabel");
		label.setEndsWith3Points(true);
		//
		addComponent(BorderLayout.CENTER, label);
		//
		try {
			if (arrow == null) {
				arrow = Image.createImage("/PullDownRefresh/arrow.png");
			}
			if (refresh == null) {
				refresh = Image.createImage("/PullDownRefresh/refresh.png");
			}
			//
			indicator = new PullDownRefreshIndicator(arrow, refresh);
			//
			addComponent(BorderLayout.WEST, indicator);
		} catch (IOException e) {
			throw new IllegalStateException(
				"Error to load /PullDownRefresh/arrow.png or refresh.png");
		}
	}
	
	/**
	 * <p>
	 * Tells the component that a refresh operation was started 
	 * programmatically.
	 * </p>
	 * <p>
	 * Call this method when an external event source triggers a programmatic 
	 * refresh of your {@link Form}. For example, if you use an {@link Timer} 
	 * object to refresh the contents of the {@link Form} periodically, you 
	 * would call this method as part of your timer handler. This method updates
	 * the state of the refresh component to reflect the in-progress refresh 
	 * operation. When the refresh operation ends, be sure to call the 
	 * {@link PullDownRefresh#endRefreshing()} method to return the component to
	 * its default state.
	 * </p>
	 * <p>
	 * This method does not trigger any action listener registered.
	 * </p>
	 */
	public void beginRefreshing() {
		if (!refreshing) {
			if (!visibleOnDefaultState) {
				setHidden(false);
			}
			//
			label.setText(stateRefreshingLabel);
			indicator.setState(PullDownRefreshIndicator.REFRESHING);
			//
			triggered = true;
			refreshing = true;
		}
	}

	/**
	 * <p>
	 * Tells the component that a refresh operation has ended.
	 * </p>
	 * <p>
	 * Call this method at the end of any refresh operation (whether it was 
	 * initiated programmatically or by the user) to return the refresh 
	 * component to its default state. If the refresh component is at least 
	 * partially visible, calling this method also hides it. If animations are 
	 * also enabled, the control is hidden using an animation.
	 * </p>
	 */
	public void endRefreshing() {
		if (refreshing) {
			if (!visibleOnDefaultState) {
				setHidden(true);
			}
			//
			label.setText(stateDefaulLabel);
			indicator.setState(PullDownRefreshIndicator.DEFAULT);
			//
			triggered = false;
			refreshing = false;
		}
	}

	/**
	 * <p>
	 * A Boolean value indicating whether a refresh component has been triggered
	 * and is in progress.
	 * </p>
	 * @return Refreshing (true).
	 */
	public boolean isRefreshing() {
		return refreshing;
	}
	
	/**
	 * <p>
	 * Adds an action listener that will be notified of a refresh operation.
	 * </p>
	 * @param listener Listener.
	 */
	public void addActionListener(ActionListener listener) {
		if (listener != null) {
			dispatcher.addListener(listener);
		}
	}
	
	/**
	 * <p>
	 * Removes an action listener.
	 * </p>
	 * @param listener Listener.
	 */
	public void removeActionListener(ActionListener listener) {
		if (listener != null) {
			dispatcher.removeListener(listener);
		}
	}
	
	/**
	 * <p>
	 * Sets the default state label.
	 * </p>
	 * @param defaultLabel Label.
	 */
	public void setStateDefaultLabel(String defaultLabel) {
		this.stateDefaulLabel = defaultLabel;
		//
		if (!triggered && !refreshing) {
			label.setText(defaultLabel);
		}
	}

	/**
	 * <p>
	 * Sets the "pulling" state label.
	 * </p>
	 * @param pullingLabel Label.
	 */
	public void setStatePullingLabel(String pullingLabel) {
		this.statePullingLabel = pullingLabel;
		//
		if (triggered && !refreshing) {
			label.setText(pullingLabel);
		}
	}

	/**
	 * <p>
	 * Sets the "refreshing" state label.
	 * </p>
	 * @param refreshingLabel Label.
	 */
	public void setStateRefreshingLabel(String refreshingLabel) {
		this.stateRefreshingLabel = refreshingLabel;
		//
		if (refreshing) {
			label.setText(refreshingLabel);
		}
	}
	
	/**
	 * <p>
	 * Sets an extra length so pulling state can be activated. Length must be
	 * >= zero.
	 * </p>
	 * @param length Length.
	 */
	public void setPullingLength(int length) {
		if (length >= 0) {
			pullingLength = length;
		}
	}

	/**
	 * @see com.sun.lwuit.Component#initComponent()
	 */
	protected void initComponent() {
		if (initialY == Integer.MIN_VALUE) {
			initialY = getAbsoluteY();
			//
			if (!visibleOnDefaultState) {
				setHidden(true);
			}
			//
			Form formParent = getComponentForm();
			//
			if (formParent != null) {
				formParent.setAlwaysTensile(true);
				//
				formParent.addPointerDraggedListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						pointerDraggedEvent();
					}
				});
				formParent.addPointerReleasedListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						pointerReleasedEvent();
					}
				});
			} else {
				throw new IllegalStateException(
					"PullDownRefresh must be added to a Form.");
			}
		}
	}
	
	/**
	 * <p>
	 * Called when a pointer dragged event is triggered.
	 * </p>
	 */
	private void pointerDraggedEvent() {
		if (!refreshing) {
			triggered = isPulling();
			//
			if (triggered) {
				if (indicator.getState() != PullDownRefreshIndicator.PULLING) {
					indicator.setState(PullDownRefreshIndicator.PULLING);
					label.setText(statePullingLabel);
				}
			} else {
				if (indicator.getState() != PullDownRefreshIndicator.DEFAULT) {
					indicator.setState(PullDownRefreshIndicator.DEFAULT);
					label.setText(stateDefaulLabel);
				}
			}
		}
	}
	
	/**
	 * <p>
	 * Called when a pointer released event is triggered.
	 * </p>
	 */
	private void pointerReleasedEvent() {
		if (!refreshing) {
			if (triggered) {
				beginRefreshing();
				fireActionListeners();
			} else {
				if (!visibleOnDefaultState) {
					setHidden(true);
				}
			}
		}
	}

	/**
	 * <p>
	 * Sets the component hidden/visible.
	 * </p>
	 * @param hidden Hidden (true).
	 */
	private void setHidden(boolean hidden) {
		Container parent = getParent();
		int absY = getAbsoluteY();
		//
		if (hidden) {
			if (absY >= (initialY - getHeight()) && absY <= initialY) {
				parent.scrollRectToVisible(
					getX(),
					getHeight(),
					parent.getWidth(),
					parent.getHeight(),
					parent);
			}
		} else {
			if (absY == (initialY - getHeight())) {
				parent.scrollRectToVisible(
					getX(),
					0,
					parent.getWidth(),
					parent.getHeight(),
					parent);
			}
		}
	}

	/**
	 * <p>
	 * A Boolean value indicating whether a refresh component has been pulled.
	 * </p>
	 * @return Pulling (true).
	 */
	private boolean isPulling() {
		int gap = initialY + pullingLength;
		//
		if (visibleOnDefaultState) {
			gap += getHeight();
		}
		//
		return getAbsoluteY() > gap;
	}
	
	/**
	 * <p>
	 * Fires action listeners.
	 * </p>
	 */
	private void fireActionListeners() {
		dispatcher.fireActionEvent(new ActionEvent(this));
	}
	
	/**
	 * <p>
	 * Pull down refresh indicator.
	 * </p>
	 */
	private final class PullDownRefreshIndicator extends Component {
		/**
		 * <p>
		 * Default state.
		 * </p>
		 */
		public static final int DEFAULT = 1;

		/**
		 * <p>
		 * Pulling state.
		 * </p>
		 */
		public static final int PULLING = 2;

		/**
		 * <p>
		 * Refreshing state.
		 * </p>
		 */
		public static final int REFRESHING = 3;
		
		/**
		 * <p>
		 * State.
		 * </p>
		 */
		private int state;

		/**
		 * <p>
		 * Sprite.
		 * </p>
		 */
		private Image[] sprite;
		
		/**
		 * <p>
		 * Refresh sprite.
		 * </p>
		 */
		private Image[] refreshSprite;
	    
		/**
		 * <p>
		 * Arrow forward sprite.
		 * </p>
		 */
		private Image[] arrowForwardSprite;
	    
		/**
		 * <p>
		 * Arrow backward sprite.
		 * </p>
		 */
		private Image[] arrowBackwardSprite;

		/**
		 * <p>
		 * In loop.
		 * </p>
		 */
		private boolean loop;
	    
		/**
		 * <p>
		 * Cursor.
		 * </p>
		 */
		private int cursor;
	    
		/**
		 * <p>
		 * Last update.
		 * </p>
		 */
		private long lastUpdate;
	    
	    /**
	     * <p>
	     * Creates a new instance of PullDownRefreshIndicator.
	     * </p>
	     * @param arrowImage Arrow image.
	     * @param refreshImage Refresh image.
	     */
	    public PullDownRefreshIndicator(Image arrowImage, Image refreshImage) {
	    	setUIID("PullDownRefreshIndicator");
	    	//
	        Image refresh45 = refreshImage.rotate(45);
	        //
	        refreshSprite = new Image[] {
	        	refreshImage,
	        	refresh45,
	        	refreshImage.rotate(90),
	        	refresh45.rotate(90),
	            refreshImage.rotate(180),
	            refresh45.rotate(180),
	            refreshImage.rotate(270),
	            refresh45.rotate(270)};
	        arrowForwardSprite = new Image[] {
	        	arrowImage,
	        	arrowImage.rotate(45),
	        	arrowImage.rotate(90),
	        	arrowImage.rotate(135),
	        	arrowImage.rotate(180)};
	        arrowBackwardSprite = new Image[] {
	        	arrowForwardSprite[4],
	        	arrowForwardSprite[3],
	        	arrowForwardSprite[2],
	        	arrowForwardSprite[1],
	        	arrowForwardSprite[0]};
	        //
	        setState(DEFAULT);
	        cursor = 4;
	    }
	    
	    /**
	     * <p>
	     * Sets the state.
	     * </p>
	     * @param state State.
	     * @see PullDownRefreshIndicator#DEFAULT
	     * @see PullDownRefreshIndicator#PULLING
	     * @see PullDownRefreshIndicator#REFRESHING
	     */
	    public void setState(int state) {
	    	if (this.state != state) {
	    		loop = false;
	    		cursor = 0;
	    		this.state = state;
	    		//
	    		if (state == DEFAULT) {
	    			sprite = arrowForwardSprite;
	    		} else if (state == PULLING) {
	    			sprite = arrowBackwardSprite;
	    		} else {
	    			sprite = refreshSprite;
	    			loop = true;
	    		}
	    		//
	    		if (getComponentForm() != null) {
	    			getComponentForm().registerAnimated(this);
	    		}
	    	}
	    }
	    
	    /**
	     * Returns the current state.
	     * @return State.
	     */
	    public int getState() {
	    	return state;
	    }
	    
	    /**
		 * @see com.sun.lwuit.Component#animate()
		 */
		public boolean animate() {
			final long INTERVAL = 50;
			//
			boolean animate = false;
		    long now = System.currentTimeMillis();
		    //
		    if ((now - lastUpdate) > INTERVAL) {
		    	cursor++;
		        animate = true;
		        lastUpdate = now;
		    }
		    //
		    return animate;
		}

		/**
	     * @see com.sun.lwuit.Component#paint(com.sun.lwuit.Graphics)
	     */
	    public void paint(Graphics g) {
	    	int index = Math.abs(cursor % sprite.length);
	    	Image img = sprite[index];
	    	//
	        g.drawImage(
	        	img,
	        	getX() + (getWidth() - img.getWidth()) / 2,
	        	getY() + (getHeight() - img.getHeight()) / 2);
	        //
	        if (!loop && index +1 == sprite.length) { //stop last frame?
	        	getComponentForm().deregisterAnimated(this);
	        }
	    }
	    
	    /**
	     * @see com.sun.lwuit.Component#initComponent()
	     */
	    protected void initComponent() {
	        getStyle().setBgTransparency(0);
	        getSelectedStyle().setBgTransparency(0);
	        getUnselectedStyle().setBgTransparency(0);
	        //
	    	getComponentForm().registerAnimated(this);
	    }
	    
	    /**
		 * @see com.sun.lwuit.Component#calcPreferredSize()
		 */
		protected Dimension calcPreferredSize() {
		    Style style = getStyle();
		    //
		    return new Dimension(
		    	refreshSprite[0].getWidth() + 
		    		style.getPadding(LEFT) + 
		    		style.getPadding(RIGHT), 
		        refreshSprite[0].getHeight() + 
		        	style.getPadding(TOP) + 
		        	style.getPadding(BOTTOM));
		}
	}
}
