package com.accounting.app.dto.common;

/**
 * API共通レスポンス
 * @param <T> データの型
 */
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ErrorInfo error;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, T data, ErrorInfo error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    /**
     * 成功レスポンスを作成
     * @param data レスポンスデータ
     * @param <T> データの型
     * @return 成功レスポンス
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /**
     * エラーレスポンスを作成
     * @param code エラーコード
     * @param message エラーメッセージ
     * @param <T> データの型
     * @return エラーレスポンス
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorInfo(code, message));
    }

    /**
     * エラーレスポンスを作成
     * @param code エラーコード
     * @param message エラーメッセージ
     * @param details エラー詳細
     * @param <T> データの型
     * @return エラーレスポンス
     */
    public static <T> ApiResponse<T> error(String code, String message, Object details) {
        return new ApiResponse<>(false, null, new ErrorInfo(code, message, details));
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    /**
     * エラー情報クラス
     */
    public static class ErrorInfo {
        private String code;
        private String message;
        private Object details;

        public ErrorInfo() {
        }

        public ErrorInfo(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public ErrorInfo(String code, String message, Object details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getDetails() {
            return details;
        }

        public void setDetails(Object details) {
            this.details = details;
        }
    }
}
