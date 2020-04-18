package de.sakpaas.backend.exception;

import org.springframework.http.HttpStatus;

public class Expect {

  /**
   * The String that will be used to split textId and parameters so it can be used in the error
   * management.
   */
  static final String SPLIT = "%%";

  /**
   * Throws an {@link ApplicationException} if the Object in param "obj" is null.
   * 
   * @param obj The object that should be non null
   * @param httpStatus response status on error
   * @param textId The ID of the error message
   * @param replacers replacers for the placeholders in the error message
   */
  public static void notNull(Object obj, HttpStatus httpStatus, String textId,
      Object... replacers) {
    if (obj == null) {
      throwError(httpStatus, textId, replacers);
    }
  }

  /**
   * Throws an {@link ApplicationException} if the Object in param "obj" is not null.
   * 
   * @param obj The object that should be null
   * @param httpStatus response status on error
   * @param textId The ID of the error message
   * @param replacers replacers for the placeholders in the error message
   */
  public static void isNull(Object obj, HttpStatus httpStatus, String textId, Object... replacers) {
    if (obj != null) {
      throwError(httpStatus, textId, replacers);
    }
  }

  /**
   * Throws an {@link ApplicationException} if the bool in param "bool" is false.
   * 
   * @param bool The boolean that should be true
   * @param httpStatus response status on error
   * @param textId The ID of the error message
   * @param replacers replacers for the placeholders in the error message
   */
  public static void isTrue(boolean bool, HttpStatus httpStatus, String textId,
      Object... replacers) {
    if (!bool) {
      throwError(httpStatus, textId, replacers);
    }
  }

  /**
   * Throws an {@link ApplicationException} if the String in param "str" is empty or null.
   * 
   * @param str The string that should have content
   * @param httpStatus response status on error
   * @param textId The ID of the error message
   * @param replacers replacers for the placeholders in the error message
   */
  public static void hasText(String str, HttpStatus httpStatus, String textId,
      Object... replacers) {
    if (str == null || str.length() == 0) {
      throwError(httpStatus, textId, replacers);
    }
  }

  /**
   * Gets called in order to receive the represented text and in order to put the replacers into the
   * placeholders inside the error text.
   * 
   * @param httpStatus status of the error that will be in the response
   * @param textId the ID of the error message that will be in the response
   * @param replacers replacers for the placeholders in the error message
   * @throws An ApplicationException with the text of the textId and the given httpStatus
   */
  public static void throwError(HttpStatus httpStatus, String textId, Object... replacers) {
    String errorMessage = "" + textId;
    for (Object i : replacers) {
      errorMessage += SPLIT + String.valueOf(i);
    }
    throw new ApplicationException(httpStatus, errorMessage);
  }

  /**
   * Used to throw an internal error that is useful for other developers. This one does not need a
   * text ID but can hold hardcoded text instead. Therefore the exception message in the console
   * will be readable
   * 
   * @param message The error message that shall be displayed to the log only
   */
  public static void throwInternalError(String message) {
    throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, message);
  }
}
