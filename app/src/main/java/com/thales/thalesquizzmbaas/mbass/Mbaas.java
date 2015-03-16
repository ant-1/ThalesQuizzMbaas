package com.thales.thalesquizzmbaas.mbass;

import android.content.Context;
import com.thales.thalesquizzmbaas.model.Quizz;
import com.thales.thalesquizzmbaas.model.Setup;
import de.greenrobot.event.EventBus;


public abstract class Mbaas {

    private static Mbaas mMbaas;
    EventBus mEventBus;

    Mbaas(){
        mEventBus = EventBus.getDefault();
    }


    public static synchronized Mbaas getInstance(Context context) {
        if (mMbaas == null) {
            mMbaas = new KinveyMbaas(context);
        }
        return mMbaas;
    }

    public static synchronized Mbaas getInstance() {
        return mMbaas;
    }

    public class MbaasEvent {
        public Object result;
        public Throwable exception;

        public boolean isSuccessfull() {
            return exception == null;
        }
    }

    public abstract String getUserId();

    public abstract String getUserName();

    //login
    public class LoginEvent extends MbaasEvent {
    }
    public abstract void login(String username, String password);

    //register
    public class RegisterEvent extends MbaasEvent {
    }
    public abstract void register(String username, String password);


    //save mail
    public class SaveMailEvent extends MbaasEvent {
    }
    public abstract void saveMail(String username, String email);

    //get quizzes
    public class GetQuizzesEvent extends MbaasEvent {
    }
    public abstract void getQuizzes();

    //create quizz
    public class CreateQuizzEvent extends MbaasEvent {
    }
    public abstract void createQuizz(Quizz quizz);

    //start quizz
    public class StartQuizzEvent extends MbaasEvent {
    }
    public abstract void startQuizz(Quizz quizz);

    //finish quizz
    public class FinishQuizzEvent extends MbaasEvent {
    }
    public abstract void finishQuizz(Quizz quizz);

    //save quizz
    public class SaveQuizzEvent extends MbaasEvent {
    }
    public abstract void saveQuizz(Quizz quizz);

    //delete quizz
    public class DeleteQuizzEvent extends MbaasEvent {
    }
    public abstract void deleteQuizz(Quizz quizz);

    //start demo
    public class StartDemoEvent extends MbaasEvent {
    }
    public abstract void startDemo();

    //start demo
    public class StopDemoEvent extends MbaasEvent {
    }
    public abstract void stopDemo();

    //get and load setup
    public abstract Setup getSetup();
    public class ReloadSetupEvent extends MbaasEvent {
    }
    public abstract void reloadSetup();

}
