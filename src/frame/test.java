package frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class test {
	public static void main(String[] args) {
		new fm();
	}

}
class fm extends JFrame {
	int orgx, orgy, endx, endy;
	Image oimage = null;
	Graphics gr = null;
	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

	public fm() {
		setVisible(true);
		setSize(500, 300);
		getGraphics().clearRect(0, 0, 500, 300);
		oimage = createImage(d.width, d.height);
		gr = oimage.getGraphics();
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				orgx = e.getX();
				orgy = e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				System.out.println("...");
			}

			public void mouseDragged(MouseEvent e) {
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				endx = e.getX();
				endy = e.getY();
				Graphics g = getGraphics();
				g.clearRect(0, 0, 500, 300);
				g.setColor(Color.red);
				g.drawRect(orgx, orgy, endx - orgx, endy - orgy);
			}
		});
	}

	public void paint(Graphics g) {
		if (gr != null) {
			g.drawImage(oimage, 0, 0, this);
		}
	}
}