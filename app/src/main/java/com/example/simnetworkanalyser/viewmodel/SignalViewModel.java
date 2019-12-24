package com.example.simnetworkanalyser.viewmodel;

import android.telephony.SignalStrength;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.simnetworkanalyser.services.MonitorService;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;


public class SignalViewModel extends ViewModel {

    private MutableLiveData<SignalStrength> signalStrengthMutableLiveData =
            new MutableLiveData<>();

    private MutableLiveData<Integer> dataConnectionStateMutableLiveData =
            new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public void observe(MonitorService.MonitorBinder binder) {

        compositeDisposable.addAll(
                binder.getSignalStrengthObservable()
                        .subscribe(new Consumer<SignalStrength>() {
                            @Override
                            public void accept(SignalStrength signalStrength) throws Exception {
                                signalStrengthMutableLiveData.postValue(signalStrength);
                            }
                        }),
                binder.getDataConnectionStateObservable()
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer state) throws Exception {
                                dataConnectionStateMutableLiveData.postValue(state);
                            }
                        })
        );
    }

    public LiveData<SignalStrength> getSignalStrengthLiveData() {
        return signalStrengthMutableLiveData;
    }

    public LiveData<Integer> getDataConnectionStateLiveData() {
        return dataConnectionStateMutableLiveData;
    }

    public void stopObserving() {
        compositeDisposable.clear();
    }

}
