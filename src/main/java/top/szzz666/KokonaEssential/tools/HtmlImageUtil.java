package top.szzz666.KokonaEssential.tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * HTML 转图片工具类
 */
public class HtmlImageUtil {

    private static volatile boolean headlessSet = false;

    public static String htmlToBase64(String html, int width) {
        BufferedImage image = renderHtml(html, width);
        if (image == null) return null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return "base64://" + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    private static BufferedImage renderHtml(String html, int width) {
        ensureHeadless();

        try {
            JEditorPane pane = new JEditorPane();
            pane.setContentType("text/html;charset=UTF-8");
            pane.setEditable(false);
            pane.setText(html);

            pane.setSize(width, Integer.MAX_VALUE);
            Dimension prefSize = pane.getPreferredSize();
            int height = Math.max(prefSize.height, 100);
            pane.setSize(width, height);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            pane.paint(g2d);
            g2d.dispose();

            return image;
        } catch (Exception e) {
            return null;
        }
    }

    private static void ensureHeadless() {
        if (!headlessSet) {
            System.setProperty("java.awt.headless", "true");
            headlessSet = true;
        }
    }
}
