package com.example.picturelsb;


import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

@Getter
@Setter
public class BitMapImage {
    public BitMapImage() {
    }

    public BitMapImage(byte[] imageBytes) {
        this.imageBytes = imageBytes;
        this.bfOffBits = readLittleEndianIntOfFourBytes(imageBytes, 10);
        this.biBitCount = readLittleEndianShortOfTwoBytes(imageBytes, 28);
        this.biWidth = readLittleEndianIntOfFourBytes(imageBytes, 18);
        this.biHeight = readLittleEndianIntOfFourBytes(imageBytes, 22);
        this.maxCapacity = getMaxCapacity();
        this.random = new Random();
    }

    private byte[] imageBytes;

    private int bfOffBits;

    private int biBitCount;

    private int biWidth;

    private int biHeight;

    private int maxCapacity; // 单位是byte

    private Random random;

    public int getMaxCapacity() {
        return biBitCount == 8 ? (biWidth * biHeight) / 8 : (biWidth * biHeight * 3) / 8;
    }

    public void embed(String data) {
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        // 输出转换为String的字节数组
        String dataString = new String(dataBytes, StandardCharsets.UTF_8);
        // 输出转换为String的字节数组
        System.out.println(dataString);
        System.out.println("The data is " + dataString.length() + " bits");
        System.out.println("The maximum capacity of the image is " + maxCapacity + " bits");
        if (dataBytes.length > maxCapacity) {
            throw new IllegalArgumentException("The data is " + (dataBytes.length ) + " bits long, which exceeds the maximum capacity of the image: " +  maxCapacity + " bits");
        }

            for (int j = 0; j < 8; j++) {
                for (int i = 0; i < dataBytes.length; i++) {
                embed1Bit(i * 8 + j, dataBytes[i] >> j & 1);
            }
        }

        if (dataBytes.length < maxCapacity) {
            for (int i = 0; i < 8; i++) {
                embed1Bit(dataBytes.length * 8 + i, 0);
            }
        }

//        addGaussianNoise(0, 10);
//        System.out.println("bit准确率为" + calculateAccuracy(dataString, extract()) + "%");
    }

    public void embed1Bit(int offset, int bit) {
        int index = bfOffBits + offset;
        int mask = 0b11111110;
        imageBytes[index] = (byte) ((imageBytes[index] & mask) | bit);
    }

    public String extract() {
        ArrayList<Byte> dataBytesList = new ArrayList<>();

        for (int i = 0; i < maxCapacity; i += 1) {
            int dataByte = 0;
            for (int j = 0; j < 8; j++) {
                dataByte |= (extract1Bit(i * 8 + j) << j);
            }
            if (dataByte == 0) {
                break;
            }
            dataBytesList.add((byte)dataByte);
        }

        byte[] dataBytes = new byte[dataBytesList.size()];
        for (int i = 0; i < dataBytesList.size(); i++) {
            dataBytes[i] = dataBytesList.get(i);
        }

        return new String(dataBytes, StandardCharsets.UTF_8);
    }


    public int extract1Bit(int offset) {
        int index = bfOffBits + offset;
        return imageBytes[index] & 1;
    }


    private int readLittleEndianShortOfTwoBytes(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF) | ((bytes[offset + 1] & 0xFF) << 8);
    }

    // 读取小端序格式的整数（4字节）
    private int readLittleEndianIntOfFourBytes(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF) | ((bytes[offset + 1] & 0xFF) << 8) | ((bytes[offset + 2] & 0xFF) << 16) | ((bytes[offset + 3] & 0xFF) << 24);
    }

    public double generateGaussianNoise(double mean, double std) {
        return mean + random.nextGaussian() * std;
    }

    public  void addGaussianNoise(double mean, double std) {
        for (int i = bfOffBits; i < imageBytes.length; i++) {
            int originalValue = imageBytes[i] & 0xFF; // 将byte转为无符号int
            int noiseValue = (int) generateGaussianNoise(mean, std);
            int newValue = originalValue + noiseValue;

            // 确保新值在0到255范围内
            if (newValue < 0) {
                newValue = 0;
            } else if (newValue > 255) {
                newValue = 255;
            }

            imageBytes[i] = (byte) newValue;
        }
    }

    public double calculateAccuracy(String original, String extracted) {
        // 转换为utf-8 比较bit的准确率
        byte[] originalBytes = original.getBytes(StandardCharsets.UTF_8);
        byte[] extractedBytes = extracted.getBytes(StandardCharsets.UTF_8);

        int totalBits = Math.max(originalBytes.length, extractedBytes.length) * 8;
        int correctBits = 0;

        for (int i = 0; i < Math.min(originalBytes.length, extractedBytes.length); i++) {
            for (int j = 0; j < 8; j++) {
                if (((originalBytes[i] >> j) & 1) == ((extractedBytes[i] >> j) & 1)) {
                    correctBits++;
                }
            }
        }

        return (double) correctBits / totalBits * 100;
    }
}
