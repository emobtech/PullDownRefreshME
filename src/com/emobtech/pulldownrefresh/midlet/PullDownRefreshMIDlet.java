package com.emobtech.pulldownrefresh.midlet;

import java.io.IOException;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.emobtech.uime.lwuit.PullDownRefresh;
import com.sun.lwuit.Command;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;

public class PullDownRefreshMIDlet extends MIDlet {

	public PullDownRefreshMIDlet() {
		Display.init(this);
		//
		try {
			Resources themeRes = Resources.open("/LWUITtheme.res");
			UIManager.getInstance().setThemeProps(
				themeRes.getTheme(themeRes.getThemeResourceNames()[0]));
		} catch (IOException e) {
		}
	}

	protected void startApp() throws MIDletStateChangeException {
		final Form form = new Form("PullDownRefresh") {};
		BorderLayout layout = new BorderLayout();
		form.setLayout(layout);
		//
		List list = new List(
			new String[] {
				"J2ME Group", "eMob Tech", "Java ME", "Java", 
				"Mobile", "Network", "LWUIT", "Eclipse", 
				"Blog", "User Interface", "Open Source", "Smartphone"
			}
		);
		list.setRenderer(new DefaultListCellRenderer());
		//
		final PullDownRefresh refresher = new PullDownRefresh();
		refresher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new Thread() {
					public void run() {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
						}
						//
						refresher.endRefreshing();
					}
				}.start();
			}
		});
		//
		form.addComponent(BorderLayout.NORTH, refresher);
		form.addComponent(BorderLayout.CENTER, list);
		form.addCommand(new Command("Exit") {
			public void actionPerformed(ActionEvent evt) {
				notifyDestroyed();
			}
		});
		//
		form.show();
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {}

	protected void pauseApp() {}
}
