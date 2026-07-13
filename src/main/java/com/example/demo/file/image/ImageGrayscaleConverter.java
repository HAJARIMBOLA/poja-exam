package com.example.demo.file.image;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

/** Converts a JPEG image file to its black &amp; white (grayscale) version. */
@Component
public class ImageGrayscaleConverter {

  public byte[] apply(File imageFile) throws IOException {
    BufferedImage original = ImageIO.read(imageFile);
    BufferedImage grayscale =
        new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    op.filter(original, grayscale);

    var outputStream = new ByteArrayOutputStream();
    ImageIO.write(grayscale, "jpg", outputStream);
    return outputStream.toByteArray();
  }
}
