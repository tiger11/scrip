package com.scrip.main.strategy.failedbreakout;

import com.scrip.main.pojo.Symbol;
import com.scrip.main.util.Util;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.*;

public class StrategyExecutor {

    public static Map<String, DecimalNum> profitPercentage = new HashMap<>();
    public static Map<String, DecimalNum> lossBracket  = new HashMap<>();
    public static Map<String, Integer> successCount = new HashMap<>();
    public static Map<String, Integer> lossCount = new HashMap<>();

    private static Map<String, Num> profitMade = new HashMap<>();
    private static Map<String, Num> lossMade = new HashMap<>();
    public static Map<String, Integer> counter = new HashMap<>();

    static final Object lockObject = new Object();

    public static void updateProfit(String scripName, Num profit) {
        synchronized (lockObject) {
            if (!profitMade.containsKey(scripName)) {
                profitMade.put(scripName, DecimalNum.valueOf(0));
            }
            profitMade.put(scripName, profitMade.get(scripName).plus(profit));
        }
    }

    public static void updateLoss(String scripName, Num loss) {
        synchronized (lockObject) {
            if (!lossMade.containsKey(scripName)) {
                lossMade.put(scripName, DecimalNum.valueOf(0));
            }
            lossMade.put(scripName, lossMade.get(scripName).plus(loss));
        }
    }

    public static void main(String[] args) throws Exception {
        List<Symbol> symbolList = Util.getSymbolList();
        //List<String> symbolList = Arrays.asList("RELIANCE");
        ArrayList<Thread> arr = new ArrayList<>();

        for (Symbol s :
                symbolList) {
            FailedBreakOut failedBreakOut = new FailedBreakOut(s.getTradingSymbol());
            Thread t = new Thread(failedBreakOut);
            t.setName(s.getTradingSymbol());
            t.start();
            arr.add(t);
        }

        for (Thread t :
                arr) {
            while (t.isAlive()) {
            }
        }

        for (Symbol s :
                symbolList) {
            System.out.println(s.getTradingSymbol()
                    + "," + counter.get(s.getTradingSymbol())
                    + "," + lossCount.get(s.getTradingSymbol())
                    + "," + successCount.get(s.getTradingSymbol())
                    + "," + lossMade.get(s.getTradingSymbol())
                    + "," + profitMade.get(s.getTradingSymbol()));
        }

        System.exit(0);
    }
}
