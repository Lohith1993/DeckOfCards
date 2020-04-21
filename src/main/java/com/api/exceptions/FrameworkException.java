/**
 * 
 */
package com.api.exceptions;

/**
 * Class represents the failed action exceptions that will be thrown
 * 
 * @author 
 * @version 1.0
 */
public class FrameworkException extends RuntimeException {

  /**
   * Serial version uid
   */
  private static final long serialVersionUID = 1L;

  /**
   * Initialize with error message
   * 
   * @param message
   */
  public FrameworkException(String message) {
    super(message);
  }

  /**
   * Initialized with error message and cause for the exceptions.
   * 
   * @param message
   * @param cause
   */
  public FrameworkException(String message, Throwable cause) {
    super(message, cause);
  }

}
