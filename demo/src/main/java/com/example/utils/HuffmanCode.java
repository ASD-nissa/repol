package com.example.utils;

import java.io.*;
import java.util.*;


public class HuffmanCode {

    private static String[] Hex = {
            "0000","0001","0010","0011",
            "0100","0101","0110","0111",
            "1000","1001","1010","1011",
            "1100","1101","1110","1111"};

    private static Map<Byte,String> huffmanCodes = new HashMap<>();


    public static void unzipFile(String source,String target) throws IOException, ClassNotFoundException {
        FileInputStream is = new FileInputStream(new File(source));
        ObjectInputStream ois = new ObjectInputStream(is);

        Map<Byte,String> huffmanCodes = (Map<Byte,String>) ois.readObject();
        byte[] sourBytes = (byte[]) ois.readObject();

        byte[] targetBytes = decode(huffmanCodes,sourBytes,true);


        FileOutputStream os = new FileOutputStream(new File(target));
        os.write(targetBytes);

        is.close();
        ois.close();
        os.close();
    }

    public static void zipFile(String source,String target) throws IOException {
        FileInputStream is = new FileInputStream(new File(source));
        byte[] sourBytes = new byte[is.available()];
        is.read(sourBytes);
        Map<Byte,String> huffmanCodes = getCodes(sourBytes);

        byte[] targetBytes = getBytes(huffmanCodes,sourBytes);

        FileOutputStream os = new FileOutputStream(new File(target));
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(huffmanCodes);
        oos.writeObject(targetBytes);

        is.close();
        os.close();
        oos.close();

    }

    public static byte[] decode(Map<Byte,String> huffmanCodes,byte[] huffmanBytes,boolean rByte){
        StringBuilder ByteCode = new StringBuilder();
        for (int i = 0; i < huffmanBytes.length; i++) {
            byte b = huffmanBytes[i];
            boolean flag = (i==huffmanBytes.length-1);
            ByteCode.append(byteToString(!flag,b));
        }

        return decodeByByteCode(huffmanCodes,ByteCode.toString());
    }

    public static String decode(Map<Byte,String> huffmanCodes,byte[] huffmanBytes){
        return new String(decode(huffmanCodes,huffmanBytes,false));
    }

    public static byte[] decode(Map<Byte,String> huffmanCodes,String HexCode,boolean rByte){
        StringBuilder byteCode = new StringBuilder();
        for(int i=0;i<HexCode.length();i++){
            if(HexCode.charAt(i)>='A'){
                byteCode.append(Hex[HexCode.charAt(i)-55]);
            }else{
                byteCode.append(Hex[HexCode.charAt(i)-48]);
            }
        }

        StringBuilder buf = new StringBuilder();
        boolean flag = false;
        for (int i = byteCode.length() - 8; i < byteCode.length(); i++) {
            if (byteCode.charAt(i) != '0')
                flag = true;
            if (flag)
                buf.append(byteCode.charAt(i));
        }
        byteCode.replace(byteCode.length() - 8, byteCode.length(), buf.toString());

        return decodeByByteCode(huffmanCodes,byteCode.toString());
    }

    public static String decode(Map<Byte,String> huffmanCodes,String HexCode){
        return new String(decode(huffmanCodes,HexCode,false));
    }

    public static byte[] getBytes(Map<Byte,String> huffmanCodes,byte[] bs){
        String BytesCode = getByteCode(huffmanCodes,bs);

        int len = (BytesCode.length() + 7)/8;
        int index = 0;
        byte[] Bytes = new byte[len];

        for(int i=0;i<BytesCode.length();i+=8){
            String ByteCode;
            if(i+8 > BytesCode.length()){
                ByteCode = BytesCode.substring(i);
            }else{
                ByteCode = BytesCode.substring(i,i+8);
            }

            Bytes[index++] = (byte)Integer.parseInt(ByteCode,2);
        }

        return Bytes;
    }

    public static String getByteCode(Map<Byte,String> huffmanCodes,byte[] bs){
        StringBuilder code = new StringBuilder();
        for(int i=0;i<bs.length;i++){
            byte b = bs[i];
            code.append(huffmanCodes.get(b));
        }
        return code.toString();
    }

    public static String getHexCode(Map<Byte,String> huffmanCodes,byte[] bs){
        String bytes = getByteCode(huffmanCodes,bs);
        StringBuilder HexCode = new StringBuilder();
        int i=bytes.length()%8;
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < 8 - i; j++) {
            s.append('0');
        }
        bytes = bytes.substring(0,bytes.length()-i)+s+bytes.substring(bytes.length()-i);
        for(i=0;i<bytes.length()/8;i++){
            for(int j=i*8;j<i*8+8;j+=4){
                String buf =  bytes.substring(j,j+4);
                for(int h=0;h<Hex.length;h++) {
                    if(Hex[h].equals(buf)){
                        h=h>=10?h+55:h+48;
                        HexCode.append ((char)h);
                    }
                }
            }
        }

        return HexCode.toString();
    }

    public static Map<Byte,String> getCodes(byte[] bytes){
        HuffmanNode root = createTree(count(bytes));
        huffmanCodes.clear();
        StringBuilder s = new StringBuilder();
        getCodes(root.left,"0",s);
        getCodes(root.right,"1",s);
        return huffmanCodes;
    }

    private static byte[] decodeByByteCode(Map<Byte,String> huffmanCodes,String ByteCode){
        Map<String,Byte> map = new HashMap<>();
        for(Map.Entry<Byte,String> entry : huffmanCodes.entrySet()){
            map.put(entry.getValue(),entry.getKey());
        }

        List<Byte> target = new ArrayList<>();

        int i = 0;
        int count = 0;
        boolean flag = true;
        while (flag) {
            count++;
            String key;
            try {
                key = ByteCode.substring(i, count);
            }catch (StringIndexOutOfBoundsException e){
                ByteCode += ByteCode+'0';
                key = ByteCode.substring(i, count);
                if(key.length() > 10)
                    throw new RuntimeException("找不到key");
            }
            Byte c = map.get(key);
            if (c != null) {
                target.add(c);
                i = count;
                flag = count == ByteCode.length()?false:true;
            }
        }

        byte[] bs = new byte[target.size()];
        for (i = 0; i < target.size(); i++) {
            bs[i] = target.get(i);
        }

        return bs;
    }

    private static String byteToString(boolean flag,byte b){
        int temp = b;

        if(flag){
            temp |= 256;
        }
        String str = Integer.toBinaryString(temp);

        if(flag){
            return str.substring(str.length()-8);
        }else{
            return str;
        }
    }

    private static Map<Byte,Integer> count(byte[] bytes){
        Map<Byte,Integer> c = new HashMap<>();

        for(int i=0;i<bytes.length;i++){
            if(c.get(bytes[i]) == null){
                c.put(bytes[i],1);
            }else{
                c.put(bytes[i],c.get(bytes[i])+1);
            }
        }

        return c;
    }

    private static HuffmanNode createTree(Map<Byte,Integer> c){
        List<HuffmanNode> HuffmanNodes = new ArrayList<>();
        for(Map.Entry<Byte,Integer> entry : c.entrySet()){
            HuffmanNodes.add(new HuffmanNode(entry.getValue(),entry.getKey()));
        }

        while (HuffmanNodes.size()>1){
            Collections.sort(HuffmanNodes);
            HuffmanNode left = HuffmanNodes.get(0);
            HuffmanNode right = HuffmanNodes.get(1);
            HuffmanNode parent = new HuffmanNode(left.value+right.value);
            parent.left = left;
            parent.right = right;
            HuffmanNodes.remove(left);
            HuffmanNodes.remove(right);
            HuffmanNodes.add(parent);
        }

        return HuffmanNodes.get(0);
    }

    private static void getCodes(HuffmanNode root,String c,StringBuilder s){
        StringBuilder s2 = new StringBuilder(s);
        s2.append(c);

        if(root != null){
            if(root.target == null){
                getCodes(root.left,"0",s2);

                getCodes(root.right,"1",s2);
            }else {
                huffmanCodes.put(root.target,s2.toString());
            }
        }
    }



}

class HuffmanNode implements Comparable<HuffmanNode>{
    int value;
    Byte target;

    HuffmanNode left;
    HuffmanNode right;

    public HuffmanNode(int v){
        this.value = v;
    }

    public HuffmanNode(int v,byte t){
        this.value = v;
        this.target = t;
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return this.value - o.value;
    }
}
