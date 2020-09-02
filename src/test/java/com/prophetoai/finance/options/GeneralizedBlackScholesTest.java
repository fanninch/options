package com.prophetoai.finance.options;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GeneralizedBlackScholesTest {

    @Before
    public void Setup(){}

    @Test
    public void getPriceTest(){
        String optType = "p";
        double assetPrice = 102.00;
        double strikePrice = 100.00;
        double timeToMaturity = 0.25;
        double intRate = 0.10;
        double divYield = 0.05;
        double costOfCarry = intRate - divYield;
        double volatility = 0.20;

        double price = GeneralizedBlackScholes.getPrice(optType, assetPrice, strikePrice,
                timeToMaturity, intRate, costOfCarry, volatility);

        Assert.assertEquals(2.5565, price, .0001);

        optType = "c";
        price = GeneralizedBlackScholes.getPrice(optType, assetPrice, strikePrice,
                timeToMaturity, intRate, costOfCarry, volatility);
        Assert.assertEquals(5.7584, price, .0001);
    }

    @Test
    public void getVegaTest() {
        String optType = "p";
        double assetPrice = 102.00;
        double strikePrice = 100.00;
        double timeToMaturity = 0.25;
        double intRate = 0.10;
        double divYield = 0.05;
        double costOfCarry = intRate - divYield;
        double volatility = 0.20;

        double vega = GeneralizedBlackScholes.getVega(assetPrice, strikePrice, timeToMaturity,
                intRate, costOfCarry, volatility);
        Assert.assertEquals(0.1874285, vega, .00001);

    }

    @Test
    public void getIVTest(){
        String optType = "p";
        double assetPrice = 102.00;
        double strikePrice = 100.00;
        double timeToMaturity = 0.25;
        double intRate = 0.10;
        double divYield = 0.05;
        double costOfCarry = intRate - divYield;
        double volatility = 0.20;
        double optionPrice = 2.5565;

        double iv = GeneralizedBlackScholes.getImpliedVolatility(optType, assetPrice, strikePrice, timeToMaturity,
                intRate, costOfCarry, optionPrice);
        Assert.assertEquals(0.20, iv, .00001);
    }



}
