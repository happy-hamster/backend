package de.sakpaas.backend.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

/**
 * Used to react to any kind of error that happens during the launch of the application.
 */
@Component
public class ErrorAttributes extends DefaultErrorAttributes {

  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorAttributes.class);
  private static final String LOG_PREFIX = "Debug:";

  // Object Key in the Response for the Frontend
  private static final String RESPONSE_OBJECT_KEY = "context";

  @AllArgsConstructor
  enum Error {
    UNKNOWN("An unknown error occured."), 
    INTERNAL("An internal error occured."), 
    PERMISSION("You do not have permission to perform this action."), 
    RESOURCE("The resource you were trying to access does not exist."), 
    AUTHENTICATION("Please login to perform this action."), 
    PARAMETER("The value for parameter %0% does not match the requirement."), 
    LOCATION("Location with ID %0% does not exist.");

    @Getter
    private String message;
  }

  /**
   * The String that is used as the prefix for the replacers.
   */
  static final String PLACEHOLDER_PRE = "%";

  /**
   * The String that is used as the suffix for the replacers.
   */
  static final String PLACEHOLDER_SUF = "%";

  /**
   * Method that is called by Spring itself, every time an error occurs. Calls the default
   * implementation by Spring + additional logic for our exception handling
   */
  @Override
  public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
    errorAttributes.remove("message"); // removal of springs default message
    Throwable throwable = super.getError(webRequest);
    // unknown error unless it gets specified in the following code
    String textId = Error.UNKNOWN.name();
    String message = Error.UNKNOWN.message;
    // unknown errors have no parameters
    Object[] parameters = new Object[0];
    // there's a few errors that have no thrown exceptions
    if (throwable != null) {
      LOGGER.debug(LOG_PREFIX, throwable);
      // if the exception is one of our exceptions
      if (throwable instanceof ApplicationException) {
        ApplicationException appException = (ApplicationException) throwable;
        if (appException.isInternal()) {
          textId = Error.INTERNAL.name();
          message = Error.INTERNAL.message;
        } else {
          // setting the given error
          textId = appException.getTextId();
          // setting the given error message
          message = appException.getMessage();
          // setting the given parameter
          parameters = appException.getParameters();
          // Having no permission to access resources
        }
      } else {
        // reacting to specific errors thrown by spring
        if (throwable instanceof AccessDeniedException) {
          textId = Error.PERMISSION.name();
          message = Error.PERMISSION.message;
        } else if (throwable instanceof MethodArgumentNotValidException) {
          textId = Error.PARAMETER.name();
          message = Error.PARAMETER.message;
          MethodArgumentNotValidException notvalid = (MethodArgumentNotValidException) throwable;
          String parameterName = notvalid.getParameter().getParameterName();
          parameters = new Object[] {parameterName};
        }
        // setting the parameters of the message
        message = addParams(message, parameters);
      }
    } else {
      // httpStatus of the error
      int errorCode = (int) errorAttributes.get("status");
      switch (errorCode) {
        // Accessing Resources that do no exist
        case 404:
          textId = Error.RESOURCE.name();
          message = Error.RESOURCE.message;
          break;
        // Accessing resources while not being logged in
        case 403:
          textId = Error.AUTHENTICATION.name();
          message = Error.AUTHENTICATION.message;
          break;
        default:
          break;
      }
    }
    // Creation and appending of the prepared error for the response
    ErrorContext errorContext = new ErrorContext(textId, parameters, message);
    errorAttributes.put(RESPONSE_OBJECT_KEY, errorContext);
    return errorAttributes;
  }

  /**
   * Used to add the parameters into the placeholders of the message.
   * 
   * @param message the message the parameters shall be filled into
   * @param parameters the parameters that shall replace the placeholders
   * @return
   */
  static String addParams(String message, Object... parameters) {
    for (int i = parameters.length - 1; i >= 0; i--) {
      message =
          message.replace(ErrorAttributes.PLACEHOLDER_PRE + i + ErrorAttributes.PLACEHOLDER_SUF,
              String.valueOf(parameters[i]));
    }
    return message;
  }

  /**
   * Class that will be serialized and send in the JSON of the response.
   */
  @AllArgsConstructor
  private class ErrorContext {

    @Getter
    private String textId; // the textId that can be matched to a message

    @Getter
    private Object[] parameters; // parameters to place inside the error message

    @Getter
    private String defaultMessage; // default error message for the frontend
  }
}


