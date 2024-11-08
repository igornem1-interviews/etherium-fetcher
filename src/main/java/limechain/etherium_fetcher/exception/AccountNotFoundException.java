package limechain.etherium_fetcher.exception;

@SuppressWarnings("serial")
public class AccountNotFoundException extends Exception {

  public AccountNotFoundException(Long id) {
    super("Could not find employee " + id);
  }
}