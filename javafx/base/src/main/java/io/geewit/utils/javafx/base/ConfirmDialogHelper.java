package io.geewit.utils.javafx.base;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * Utility class for showing confirmation dialogs and executing actions
 * using JDK Flow + virtual threads, with a builder-style API.
 */
@Slf4j
@SuppressWarnings({"unused"})
public final class ConfirmDialogHelper {

    private ConfirmDialogHelper() {
    }

    /**
     * 获取一个新的 ConfirmBuilder。
     */
    public static ConfirmBuilder builder() {
        return new ConfirmBuilder();
    }

    // ========== Builder ==========

    public static final class ConfirmBuilder {
        private Window owner;
        private String message;
        private Runnable okAction;
        private Runnable cancelAction;
        private String title;
        private String headerText;

        private ConfirmBuilder() {
        }

        public ConfirmBuilder owner(Window owner) {
            this.owner = owner;
            return this;
        }

        public ConfirmBuilder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * 可选：设置对话框标题。
         */
        public ConfirmBuilder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * 可选：设置对话框头部文本（不设置则为 null）。
         */
        public ConfirmBuilder header(String headerText) {
            this.headerText = headerText;
            return this;
        }

        /**
         * 配置 OK 按钮被点击后执行的逻辑（核心业务逻辑）。
         */
        public ConfirmBuilder onOk(Runnable okAction) {
            this.okAction = okAction;
            return this;
        }

        /**
         * 可选：配置 Cancel 按钮被点击后执行的逻辑。
         */
        public ConfirmBuilder onCancel(Runnable cancelAction) {
            this.cancelAction = cancelAction;
            return this;
        }

        /**
         * 构建并显示对话框：
         * - 为本次对话框创建一个私有 SubmissionPublisher
         * - OK / CANCEL 的任务都通过该 Publisher 分发，在虚拟线程中执行
         * - 任务完成后自动关闭对话框，并 close Publisher
         */
        public void show() {
            Objects.requireNonNull(message, "message must not be null");
            Objects.requireNonNull(okAction, "okAction must not be null");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
            if (owner != null) {
                alert.initOwner(owner);
            }

            if (title != null) {
                alert.setTitle(title);
            }
            alert.setHeaderText(headerText); // 可以为 null

            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);

            // 每个对话框有自己的 Publisher 和 Subscriber
            SubmissionPublisher<Runnable> publisher = this.getSubmissionPublisher();

            // OK：异步执行 + 完成后自动关闭对话框 + 关闭 Publisher
            okButton.addEventFilter(ActionEvent.ACTION, event -> {
                event.consume();

                okButton.setDisable(true);
                if (cancelButton != null) {
                    cancelButton.setDisable(true);
                }

                publisher.submit(() -> {
                    try {
                        okAction.run();
                    } catch (Throwable ex) {
                        log.error("Error in publisher OK task", ex);
                    } finally {
                        Platform.runLater(alert::close);
                        publisher.close();
                    }
                });
            });

            // CANCEL：无论是否有 cancelAction，都统一走这里，确保 Publisher 也能关闭
            if (cancelButton != null) {
                cancelButton.addEventFilter(ActionEvent.ACTION, event -> {
                    event.consume();

                    okButton.setDisable(true);
                    cancelButton.setDisable(true);

                    publisher.submit(() -> {
                        try {
                            if (cancelAction != null) {
                                cancelAction.run();
                            }
                        } catch (Throwable ex) {
                            log.error("Error in publisher CANCEL task", ex);
                        } finally {
                            Platform.runLater(alert::close);
                            publisher.close();
                        }
                    });
                });
            }

            alert.show();
        }

        private @NonNull SubmissionPublisher<Runnable> getSubmissionPublisher() {
            SubmissionPublisher<Runnable> publisher = new SubmissionPublisher<>();

            publisher.subscribe(new Flow.Subscriber<>() {

                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    // 不需要保存为字段，直接请求即可
                    subscription.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(Runnable item) {
                    if (item != null) {
                        Thread.startVirtualThread(item);
                    }
                }

                @Override
                public void onError(Throwable ex) {
                    log.error("Error in subscriber", ex);
                }

                @Override
                public void onComplete() {
                    // no-op
                }
            });
            return publisher;
        }
    }
}
