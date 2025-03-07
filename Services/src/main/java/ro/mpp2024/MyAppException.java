package ro.mpp2024;

public class MyAppException extends Exception{
        public MyAppException() {
        }

        public MyAppException(String message) {
            super(message);
        }

        public MyAppException(String message, Throwable cause) {
            super(message, cause);
        }
    }
