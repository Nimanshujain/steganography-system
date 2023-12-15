import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageSteganography {

    public static void main(String[] args) {
        // Encode message into an image
        encodeMessage("input_image.png", "Hello, World!", "encoded_image.png");

        // Decode message from the encoded image
        String decodedMessage = decodeMessage("encoded_image.png");
        System.out.println("Decoded Message: " + decodedMessage);
    }

    
    public static void encodeMessage(String inputImagePath, String message, String outputImagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(inputImagePath));
            int width = image.getWidth();
            int height = image.getHeight();

            int messageLength = message.length();
            int charIndex = 0;

            for (int y = 0; y < height && charIndex < messageLength; y++) {
                for (int x = 0; x < width && charIndex < messageLength; x++) {
                    int pixel = image.getRGB(x, y);
                    int alpha = (pixel >> 24) & 0xFF;
                    int red = (pixel >> 16) & 0xFF;
                    int green = (pixel >> 8) & 0xFF;
                    int blue = pixel & 0xFF;

                    int charValue = message.charAt(charIndex);
                    for (int bit = 7; bit >= 0; bit--) {
                        int bitValue = (charValue >> bit) & 1;
                        red = (red & 0xFE) | bitValue;
                        charIndex++;
                        if (charIndex >= messageLength)
                            break;
                    }

                    int encodedPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    image.setRGB(x, y, encodedPixel);
                }
            }

            File outputImageFile = new File(outputImagePath);
            ImageIO.write(image, "png", outputImageFile);
            System.out.println("Message encoded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static String decodeMessage(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();

            StringBuilder message = new StringBuilder();
            int charValue = 0;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, y);
                    int red = (pixel >> 16) & 0xFF;

                    charValue = (charValue << 1) | (red & 1);

                    if (x % 8 == 7) {
                        message.append((char) charValue);
                        if (charValue == 0)
                            return message.toString();
                        charValue = 0;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
