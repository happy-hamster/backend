package de.sakpaas.backend.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

/**
 * Used to react to any kind of error that happens during the launch of the application.
 */
@Component
public class ErrorAttributes extends DefaultErrorAttributes {

  // Object Key in the Response for the Frontend
  private static final String RESPONSE_OBJECT_KEY = "AppError";

  // Error types that are needed for specific reaction at the frontend
  private static final String DEFAULT_ERROR_TYPE = "error";
  private static final String UNKNOWN_ERROR_TYPE = "unknown";
  private static final String INTERNAL_ERROR_TYPE = "internal";
  private static final String NO_PERMISSION_ERROR_TYPE = "permission";
  private static final String UNKNOWN_RESOURCE_ERROR_TYPE = "resource";
  private static final String NOT_AUTHENTICATED_ERROR_TYPE = "authentication";


  // TextIds that are used inside this class
  private static final String UNKNOWN_ERROR_ID = "unknown_error";
  private static final String INTERNAL_ERROR_ID = "internal_error";
  private static final String NO_PERMISSION_ERROR_ID = "no_permission";
  private static final String UNKNOWN_RESOURCE_ID = "unknown_resource";
  private static final String NOT_AUTHENTICATED_ID = "not_authenticated";

  @Value("${app.exceptions.debug}")
  private boolean printException;


  /**
   * The String that will be the prefix for the parameters.
   */
  private static final String SPLIT = "%%";

  /**
   * Returns the message that corresponds to the given textId.
   * 
   * @param textId the ID of the message that shall be received
   * @return the corresponding message
   */
  private String getErrorMessage(String textId) {
    // TODO Should be implemented in any kind of text management as soon as it exists
    switch (textId) {
      case UNKNOWN_ERROR_ID:
        return "Ein unbekannter Fehler ist aufgetreten.";
      case INTERNAL_ERROR_ID:
        return "Ein interner Fehler ist aufgetreten.";
      case NO_PERMISSION_ERROR_ID:
        return "Du hast keine Berechtigung dies zu tun.";
      case UNKNOWN_RESOURCE_ID:
        return "Die angeforderte Resource existiert nicht.";
      case "no_location":
        return "Es existiert keine Location mit der ID %%0, %%1, %%2";
      default:
        return null;
    }
  }

  /**
   * Eine Methode, die von Spring automatisch aufgerufen wird. Hierbei wird die default-Methode
   * aufgerufen und zusätzlich wird unser eigenes Errorhandling durchgeführt. (FeedbackManager)
   */
  @Override
  public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
    errorAttributes.remove("message"); // removal of springs default message
    Throwable throwable = super.getError(webRequest);
    // unknown error unless it gets specified in the following code
    String message = getErrorMessage(UNKNOWN_ERROR_ID);
    // default error unless it gets specified in the following code
    String errorType = UNKNOWN_ERROR_TYPE;
    // there's a few errors that have no exceptions
    if (throwable != null) {
      if (printException) {
        throwable.printStackTrace();
      }
      // if the exception is one of our exceptions we have to replace the textId
      if (throwable instanceof ApplicationException) {
        ApplicationException appException = (ApplicationException) throwable;
        // internal error -> not communicated to frontend
        if (appException.isInternal()) {
          message = getErrorMessage(INTERNAL_ERROR_ID);
          errorType = INTERNAL_ERROR_TYPE;
        } else {
          // getting the message of the corresponding textId
          message = getErrorMessage(appException.getTextId());
          Object[] replacers = appException.getReplacers();
          errorType = DEFAULT_ERROR_TYPE;
          // setting the parameters of the message
          for (int i = replacers.length - 1; i >= 0; i--) {
            message = message.replace(SPLIT + i, String.valueOf(replacers[i]));
          }
        }
      } else if (throwable instanceof AccessDeniedException) {
        message = getErrorMessage(NO_PERMISSION_ERROR_ID);
        errorType = NO_PERMISSION_ERROR_TYPE;
      }
    } else {
      // httpStatus of the error
      int errorCode = (int) errorAttributes.get("status");
      switch (errorCode) {
        // Accessing Resources that do no exist
        case 404:
          message = getErrorMessage(UNKNOWN_RESOURCE_ID);
          errorType = UNKNOWN_RESOURCE_ERROR_TYPE;
          break;
        case 403:
          message = getErrorMessage(NOT_AUTHENTICATED_ID);
          errorType = NOT_AUTHENTICATED_ERROR_TYPE;
          break;
        default:
          break;
      }
    }
    // Creation and appending of the prepared error for the response
    AppError appError = new AppError(errorType, message);
    errorAttributes.put(RESPONSE_OBJECT_KEY, appError);
    return errorAttributes;
  }

  /**
   * Class that will be serialized and sent in the JSON of the response.
   */
  @AllArgsConstructor
  class AppError {

    @Getter
    private String errorType; // used to react to specific errors on the frontend (e.g needs login)

    @Getter
    private String message; // error message that will be shown on the frontend
  }
}


