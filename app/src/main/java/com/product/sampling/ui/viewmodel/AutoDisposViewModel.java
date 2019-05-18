package com.product.sampling.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class AutoDisposViewModel extends ViewModel {


    public AutoDisposViewModel() {

    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public void addDispos(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    protected void clearCompositeDisposable() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        clearCompositeDisposable();
    }
}
