package marker;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class CircleTagIO {
	
	private CircleTagIO() {}
	
	public static void saveAsImage(CircleTag circleTag, String url, String formatName) throws IOException{
		boolean
		isMarkerBodyVisible	= circleTag.isMarkerBodyVisible(),
		isOrbitVisible = circleTag.isOrbitsVisible();
		
		int size = circleTag.getTagSize() + 1;
		
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) img.createGraphics();
		circleTag.setMarkerBodyVisible(false);
		circleTag.setOrbitsVisible(false);
		circleTag.draw(g2d);
		
		File img_file;
		try {
			img_file = new File(url + "." + formatName);
			
			if(!img_file.getParentFile().exists()) {
				img_file.getParentFile().mkdirs();
			}
			
			ImageIO.write(img, formatName, img_file);
		} catch (IOException e) {
			throw e;
		}
		finally {
			circleTag.setMarkerBodyVisible(isMarkerBodyVisible);
			circleTag.setOrbitsVisible(isOrbitVisible);
		}
	}
}
