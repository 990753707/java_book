package com.gk.study.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import java.util.UUID;

/**
 * 产生随机数据
 */
public class RandomUtils {

    static String template_including_special_characters = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM+-+!/%$#&";
    static String template_only_letter = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM0123456789";

    public static String  randomStr(int length){

        return RandomStringUtils.random(length,template_only_letter);
    }
    public static String  randomStr(int length,String template){

        return RandomStringUtils.random(length,template);
    }
    UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();

    /**
     *  0<= x <= max
     * @param max
     * @return
     */
    public int randomInt(int max){
        return rng.nextInt(0,max+1);
    }
    /**
     *  min <= x <= max
     * @param max
     * @return
     */
    public int randomInt(int min,int max){
        return rng.nextInt(min,max+1);
    }

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
