package limechain.ethereum_fetcher.utils;

public class Generator {

    public static String generateOtpCode() {
        return String.valueOf((int) Math.floor(Math.random() * (999999 - 100000 + 1) + 100000));
    }

}
