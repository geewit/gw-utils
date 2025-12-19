package io.geewit.utils.javafx.base.scheduler;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import org.jspecify.annotations.NonNull;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 在虚拟线程上执行任务，并把UI更新发布回JavaFX线程。
 */
public record FxAsyncActions(FxScheduler fxScheduler,
                             VirtualThreadScheduler virtualScheduler) {

    public FxAsyncActions {
        Objects.requireNonNull(fxScheduler, "fxScheduler");
        Objects.requireNonNull(virtualScheduler, "virtualScheduler");
    }

    /**
     * 运行一个 Mono 任务（onSuccess = Mono 的结果）
     */
    @SuppressWarnings("unused")
    public <T> Disposable runMono(Supplier<Mono<@NonNull T>> taskSupplier,
                                  Node trigger,
                                  ProgressIndicator indicator,
                                  Supplier<?> onStart,
                                  Consumer<? super T> onSuccess,
                                  Consumer<? super Throwable> onError,
                                  Supplier<?> onFinally) {
        // 允许传 null；不改变原有签名
        return this.runReactive(
                taskSupplier,
                trigger,
                indicator,
                onStart,
                onSuccess,
                onError,
                onFinally);
    }

    /**
     * 运行一个 Flux 任务（onNext = Flux 的每个元素）
     */
    @SuppressWarnings("unused")
    public <T> void runFlux(Supplier<Flux<@NonNull T>> taskSupplier,
                            Node trigger,
                            ProgressIndicator indicator,
                            Supplier<?> onStart,
                            Consumer<? super T> onNext,
                            Consumer<? super Throwable> onError,
                            Supplier<?> onFinally) {
        this.runReactive(
                taskSupplier,
                trigger,
                indicator,
                onStart,
                onNext,
                onError,
                onFinally);
    }

    // -------------------- 去重后的核心实现 --------------------
    @SuppressWarnings("unused")
    private <T> Disposable runReactive(Supplier<? extends Publisher<? extends T>> taskSupplier,
                                 Node trigger,
                                 ProgressIndicator indicator,
                                 Supplier<?> onStart,
                                 Consumer<? super T> onNext,
                                 Consumer<? super Throwable> onError,
                                 Supplier<?> onFinally) {

        // —— 关键修复：显式局部变量，避免 Optional 引发的通配符捕获冲突 ——
        final Publisher<? extends T> source;
        if (taskSupplier == null) {
            source = Flux.empty();
        } else {
            Publisher<? extends T> p = taskSupplier.get();
            source = (p != null) ? p : Flux.empty();
        }

        // NOP 回调（集中做空安全）
        Supplier<?> sOnStart = (onStart != null) ? onStart : FxAsyncActions::noop;
        Consumer<? super T> sOnNext = (onNext != null) ? onNext : _ -> {};
        Consumer<? super Throwable> sOnError = (onError != null) ? onError : _ -> {};
        Supplier<?> sOnFinally = (onFinally != null) ? onFinally : FxAsyncActions::noop;

        return Flux.from(source)
                .subscribeOn(virtualScheduler.scheduler())
                .publishOn(fxScheduler.scheduler())
                .doOnSubscribe(_ -> this.toggleUi(trigger, indicator, true, sOnStart))
                .doFinally(_ -> this.toggleUi(trigger, indicator, false, sOnFinally))
                .subscribe(sOnNext, sOnError);
    }

    private static Object noop() {
        return null;
    }

    // -------------------- UI 辅助 --------------------

    private void toggleUi(Node trigger,
                          ProgressIndicator indicator,
                          boolean busy,
                          Supplier<?> extra) {
        this.runOnFx(() -> {
            this.setUiState(trigger, indicator, busy);
            if (extra != null) {
                extra.get();
            }
        });
    }

    private void runOnFx(Runnable action) {
        if (action == null) {
            return;
        }
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    private void setUiState(Node trigger,
                            ProgressIndicator indicator,
                            boolean busy) {
        if (trigger != null) trigger.setDisable(busy);
        if (indicator != null) {
            indicator.setVisible(busy);
            indicator.setManaged(busy);
            indicator.setProgress(busy ? ProgressIndicator.INDETERMINATE_PROGRESS : 0);
        }
    }
}
