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
   * @param message the message shown in the console/log
   * @param httpStatus response status on error
   * @param textId The ID of the error message
   * @param replacers replacers for the placeholders in the error message
   */
  public static void notNull(Object obj, String message, HttpStatus httpStatus, String textId,
      Object... replacers) {
    if (obj == null) {
      throwError(httpStatus, message, textId, replacers);
    }
  }

  /**
   * Throws an {@link ApplicationException} if the Object in param "obj" is not null.
   * 
   * @param obj The object that should be null
   * @param message the message shown in the console/log
   * @param httpStatus response status on error
   * @param textId The ID of the error message
   * @param replacers replacers for the placeholders in the error message
   */
  public static void isNull(Object obj, String message, HttpStatus httpStatus, String textId,
      Object... replacers) {
    if (obj != null) {
      throwError(httpStatus, message, textId, replacers);
    }
  }

  /**
   * Throws an {@link ApplicationException} if the bool in param "bool" is false.
   * 
   * @param bool The boolean that should be true
   * @param message the message shown in the console/log
   * @param httpStatus response status on error
   * @param textId The ID of the error message
   * @param replacers replacers for the placeholders in the error message
   */
  public static void isTrue(boolean bool, String message, HttpStatus httpStatus, String textId,
      Object... replacers) {
    if (!bool) {
      throwError(httpStatus, message, textId, replacers);
    }
  }

  /**
   * Throws an {@link ApplicationException} if the String in param "str" is empty or null.
   * 
   * @param str The string that should have content
   * @param message the message shown in the console/log
   * @param httpStatus response status on error
   * @param textId The ID of the error message
   * @param replacers replacers for the placeholders in the error message
   */
  public static void hasText(String str, String message, HttpStatus httpStatus, String textId,
      Object... replacers) {
    if (str == null || str.length() == 0) {
      throwError(httpStatus, message, textId, replacers);
    }
  }

  /**
   * Gets called in order to receive the represented text and in order to put the replacers into the
   * placeholders inside the error text.
   * 
   * @param httpStatus status of the error that will be in the response
   * @param message the message shown in the console/log
   * @param textId the ID of the error message that will be in the response
   * @param replacers replacers for the placeholders in the error message
   * @throws An ApplicationException with the text of the textId and the given httpStatus
   */
  public static void throwError(HttpStatus httpStatus, String message, String textId,
      Object... replacers) {
    throw new ApplicationException(httpStatus, message, textId, replacers);
  }

  /**
   * Used to throw an internal error that is useful for other developers. This one does not need a
   * text ID and will send a default message to the frontend
   * 
   * @param message the message shown in the console/log
   */
  public static void throwInternalError(String message) {
    throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
  }
}
