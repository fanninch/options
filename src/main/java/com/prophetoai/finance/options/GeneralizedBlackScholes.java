package com.prophetoai.finance.options;


import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.logging.Logger;

public final class GeneralizedBlackScholes {

    private final static Logger LOGGER = Logger.getLogger(GeneralizedBlackScholes.class.getName());

    private GeneralizedBlackScholes(){}

    /**
     * Computes price of an option using Generalized Black Scholes.  This formula will accept dividends in the
     * from of costOfCarry.  Input costOfCarry as intRate - continuous dividend yield.  Assumes borrow cost is
     * same as interest rates.
     * @param optionType "c" or "p"
     * @param assetPrice current price of underlying asset
     * @param strikePrice strike price of option
     * @param timeToExpiration time to expiration as a double.  format gives user the option of 365 day or 252 day year.
     * @param intRate continuous interest rate, generally the user's funding rate and/or rates extracted from options
     *                markets. prevailing cash rates are often used.
     * @param costOfCarry interest rate - continuous dividend yield
     * @param volatility annualized volatility of the option
     * @return option price
     */
    public static double getPrice(String optionType, double assetPrice,
                                  double strikePrice, Double timeToExpiration,
                                  double intRate, double costOfCarry, double volatility){
        double d1 = (Math.log(assetPrice / strikePrice) + (costOfCarry + Math.pow(volatility, 2) / 2) *
                timeToExpiration) / (volatility * Math.sqrt(timeToExpiration));
        double d2 = d1 - volatility * Math.sqrt(timeToExpiration);

        NormalDistribution stdNormDist = new NormalDistribution(0, 1);

        double price = -99.99;
        double netRate = Math.exp((costOfCarry - intRate) * timeToExpiration);
        if(optionType.toLowerCase().equals("c")){
            price = assetPrice * netRate *
                    stdNormDist.cumulativeProbability(d1) - strikePrice *
                    Math.exp(-intRate * timeToExpiration) * stdNormDist.cumulativeProbability(d2);
        }
        else if(optionType.toLowerCase().equals("p")){
            price = -assetPrice * netRate *
                    stdNormDist.cumulativeProbability(-d1) + strikePrice *
                    Math.exp(-intRate * timeToExpiration) * stdNormDist.cumulativeProbability(-d2);
        }
        else{
            LOGGER.warning("Invalid Input for option type: " + optionType);
        }

        return price;
    }

    public static double getImpliedVolatility(String optionType, double assetPrice, double strikePrice,
                                              double timeToExpiration, double intRate, double costOfCarry,
                                              double optionPrice){
        double epsilon = .00001;

        double iv = Math.sqrt(Math.abs(Math.log(assetPrice / strikePrice) + intRate * timeToExpiration) * 2 / timeToExpiration);
        double initialOptPrice = GeneralizedBlackScholes.getPrice(optionType, assetPrice, strikePrice, timeToExpiration,
                intRate, costOfCarry, iv);
        double seedVega = GeneralizedBlackScholes.getVega(assetPrice, strikePrice,
                timeToExpiration, intRate, costOfCarry, iv);
        double minDiff = Math.abs(optionPrice - initialOptPrice);

        while( Math.abs( optionPrice - initialOptPrice) >= epsilon && Math.abs(optionPrice - initialOptPrice) <= minDiff){
            iv = iv - (initialOptPrice - optionPrice) / (seedVega * 100.0);
            initialOptPrice = GeneralizedBlackScholes.getPrice(optionType, assetPrice, strikePrice, timeToExpiration,
                    intRate, costOfCarry, iv);
            seedVega = GeneralizedBlackScholes.getVega(assetPrice, strikePrice,
                    timeToExpiration, intRate, costOfCarry, iv);
            minDiff = Math.abs(optionPrice - initialOptPrice);
        }
        return iv;
    }

    public static double getVega(double assetPrice, double strikePrice, double timeToExpiration, double intRate,
                          double costOfCarry, double impliedVolatility){
        NormalDistribution stdNormDist = new NormalDistribution(0, 1);


        double d1 = (Math.log(assetPrice / strikePrice) + (costOfCarry + Math.pow(impliedVolatility, 2) / 2 ) * timeToExpiration ) /
                (impliedVolatility * Math.sqrt(timeToExpiration));

        return assetPrice * Math.exp((costOfCarry - intRate) *
                timeToExpiration) * stdNormDist.density(d1) * Math.sqrt(timeToExpiration) / 100.0;
    }




}
