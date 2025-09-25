package currency;

import java.util.InputMismatchException;
import java.util.Scanner;


class Currency {
    private final String name;
    private final double rate;

    public Currency(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }
}


class ExchangeRate {
    private static ExchangeRate instance;

    // Все валюты хранятся в значении относительно RUB
    private static final Currency USD = new Currency("USD", 83.9914);
    private static final Currency AUD = new Currency("AUD", 55.6443);
    private static final Currency AZN = new Currency("AZN", 49.4067);
    private static final Currency BHD = new Currency("BHD", 223.3331);
    private static final Currency RUB = new Currency("RUB", 1);

    private static final Currency[] exchangeRates = { USD, AUD, AZN, BHD, RUB };

    private ExchangeRate() {}

    public static ExchangeRate getInstance() {
        if (instance == null) {
            instance = new ExchangeRate();
        }
        return instance;
    }

    public Currency getCurrencyByName(String name) {
        for (Currency c : exchangeRates) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public Currency[] getCurrency() {
        return exchangeRates;
    }
}


class CurrencyConverter {
    private final ExchangeRate exchangeRate = ExchangeRate.getInstance();

    public double convert(double amount, String from, String to) {
        Currency cFrom = exchangeRate.getCurrencyByName(from);
        Currency cTo = exchangeRate.getCurrencyByName(to);
        return amount * (cFrom.getRate() / cTo.getRate());
    }
}


class CurrencyApp {
    private final Scanner scanner = new Scanner(System.in);
    private final ExchangeRate exchangeRate = ExchangeRate.getInstance();
    private final CurrencyConverter converter = new CurrencyConverter();

    public void run() {
        printAvailableCurrencies();

        System.out.print("Из какой валюты: ");
        String from = scanner.nextLine();
        while (exchangeRate.getCurrencyByName(from) == null) {
            System.out.print("Введите валюту из списка доступных: ");
            from = scanner.nextLine();
        }

        System.out.print("В какую валюту: ");
        String to = scanner.nextLine();
        while (exchangeRate.getCurrencyByName(to) == null) {
            System.out.print("Введите валюту из списка доступных: ");
            to = scanner.nextLine();
        }

        double amount = 0;
        while (true) {
            System.out.print("Сумма: ");
            try {
                amount = scanner.nextDouble();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ожидается вещественное число.");
                scanner.nextLine();
            }
        }

        double result = converter.convert(amount, from, to);
        System.out.println(amount + " " + from.toUpperCase() + " = " + result + " " + to.toUpperCase());
    }

    private void printAvailableCurrencies() {
        System.out.println("Доступные валюты:");
        for (Currency c : exchangeRate.getCurrency()) {
            System.out.println("- " + c.getName());
        }
        System.out.println();
    }
}


public class Main2 {
    public static void main(String[] args) {
        new CurrencyApp().run();
    }
}
