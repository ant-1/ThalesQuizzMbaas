package com.thales.thalesquizzmbaas.mbass;

import android.content.Context;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyDeleteResponse;
import com.thales.thalesquizzmbaas.model.Quizz;
import com.thales.thalesquizzmbaas.model.Setup;
import com.thales.thalesquizzmbaas.utils.ThalesQuizzMbaasConstants;

import java.util.ArrayList;
import java.util.List;

public class KinveyMbaas extends Mbaas {

    private Client mKinveyClient;

    private Setup mSetup;
    private static final String ENT_QUIZZES = "quizzes";
    private static final String ENT_SETUP = "setup";

    protected KinveyMbaas(Context context) {
        super();
        mKinveyClient = new Client.Builder(context.getApplicationContext()).build();//new Client.Builder("kid_eexdwAUSli","7139ee1b3bf444d6b9e8c682b6eabff1",this).build();
        loadSetup();
    }

    private boolean loadSetup() {
        if (mSetup == null) {
            mKinveyClient.appData(ENT_SETUP, Setup.class).get(new KinveyListCallback<Setup>() {
                @Override
                public void onSuccess(Setup[] setups) {
                    if (setups != null && setups.length == 1) {
                        mSetup = setups[0];
                    } else {
                        mSetup = new Setup();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Throwable test = throwable;
                    mSetup = null;
                }
            });
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getUserId() {
        return mKinveyClient.user().getId();
    }

    @Override
    public String getUserName() {
        return mKinveyClient.user().getUsername();
    }


    @Override
    public void login(String username, String password) {
        mKinveyClient.user().logout().execute();
        mKinveyClient.user().login(username, password, new KinveyUserCallback() {
            @Override
            public void onSuccess(User user) {
                LoginEvent event = new LoginEvent();
                event.result = user;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                LoginEvent event = new LoginEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void register(String username, String password) {
        mKinveyClient.user().logout().execute();
        mKinveyClient.user().create(username, password, new KinveyUserCallback() {
            @Override
            public void onSuccess(User user) {
                RegisterEvent event = new RegisterEvent();
                event.result = user;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                RegisterEvent event = new RegisterEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void saveMail(String username, String email) {
        mKinveyClient.user().put("email", email);
        mKinveyClient.user().update(new KinveyUserCallback() {
            @Override
            public void onSuccess(User user) {
                SaveMailEvent event = new SaveMailEvent();
                event.result = user;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                SaveMailEvent event = new SaveMailEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void getQuizzes() {
        Query orQuery = new Query();
        orQuery.equals("_acl.creator", mKinveyClient.user().getId());
        Query query = new Query();
        query.equals("status", ThalesQuizzMbaasConstants.STATUS_STARTED).or(orQuery);

        mKinveyClient.appData(ENT_QUIZZES, Quizz.class).get(query, new KinveyListCallback<Quizz>() {
            @Override
            public void onSuccess(Quizz[] quizzs) {
                List<Quizz> userResults = new ArrayList<Quizz>();
                for (Quizz quizz : quizzs) {
                    userResults.add(quizz);
                }
                GetQuizzesEvent event = new GetQuizzesEvent();
                event.result = userResults;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                GetQuizzesEvent event = new GetQuizzesEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void createQuizz(Quizz quizz) {
        mKinveyClient.appData(ENT_QUIZZES, Quizz.class).save(quizz, new KinveyClientCallback<Quizz>() {
            @Override
            public void onSuccess(Quizz quizz) {
                CreateQuizzEvent event = new CreateQuizzEvent();
                event.result = quizz;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                CreateQuizzEvent event = new CreateQuizzEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void startQuizz(Quizz quizz) {
        quizz.setStatus(ThalesQuizzMbaasConstants.STATUS_STARTED);
        mKinveyClient.appData(ENT_QUIZZES, Quizz.class).save(quizz, new KinveyClientCallback<Quizz>() {
            @Override
            public void onSuccess(Quizz quizz) {
                StartQuizzEvent event = new StartQuizzEvent();
                event.result = quizz;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                StartQuizzEvent event = new StartQuizzEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void finishQuizz(Quizz quizz) {
        quizz.setStatus(ThalesQuizzMbaasConstants.STATUS_FINISHED);
        mKinveyClient.appData(ENT_QUIZZES, Quizz.class).save(quizz, new KinveyClientCallback<Quizz>() {
            @Override
            public void onSuccess(Quizz quizz) {
                FinishQuizzEvent event = new FinishQuizzEvent();
                event.result = quizz;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                FinishQuizzEvent event = new FinishQuizzEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void saveQuizz(Quizz quizz) {
        mKinveyClient.appData(ENT_QUIZZES, Quizz.class).save(quizz, new KinveyClientCallback<Quizz>() {
            @Override
            public void onSuccess(Quizz quizz) {
                SaveQuizzEvent event = new SaveQuizzEvent();
                event.result = quizz;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                SaveQuizzEvent event = new SaveQuizzEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void deleteQuizz(final Quizz quizz) {
        mKinveyClient.appData(ENT_QUIZZES, Quizz.class).delete(quizz.getId(), new KinveyDeleteCallback() {

            @Override
            public void onSuccess(KinveyDeleteResponse kinveyDeleteResponse) {
                DeleteQuizzEvent event = new DeleteQuizzEvent();
                event.result = quizz;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                DeleteQuizzEvent event = new DeleteQuizzEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void startDemo() {
        if (!loadSetup()) {
            return;
        }
        mSetup.setDemoStarted(true);
        mKinveyClient.appData(ENT_SETUP, Setup.class).save(mSetup, new KinveyClientCallback<Setup>() {

            @Override
            public void onSuccess(Setup setup) {
                mSetup = setup;
                StopDemoEvent event = new StopDemoEvent();
                event.result = setup;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                StopDemoEvent event = new StopDemoEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void stopDemo() {
        if (!loadSetup()) {
            return;
        }
        mSetup.setDemoStarted(false);
        mKinveyClient.appData(ENT_SETUP, Setup.class).save(mSetup, new KinveyClientCallback<Setup>() {

            @Override
            public void onSuccess(Setup setup) {
                mSetup = setup;
                StopDemoEvent event = new StopDemoEvent();
                event.result = setup;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                StopDemoEvent event = new StopDemoEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }

    @Override
    public Setup getSetup() {
        loadSetup();
        return mSetup;
    }

    @Override
    public void reloadSetup() {
        mKinveyClient.appData(ENT_SETUP, Setup.class).get(new KinveyListCallback<Setup>() {
            @Override
            public void onSuccess(Setup[] setups) {
                if (setups != null && setups.length == 1) {
                    mSetup = setups[0];
                } else {
                    mSetup = new Setup();
                }
                ReloadSetupEvent event = new ReloadSetupEvent();
                event.result = mSetup;
                mEventBus.post(event);
            }

            @Override
            public void onFailure(Throwable throwable) {
                mSetup = null;
                ReloadSetupEvent event = new ReloadSetupEvent();
                event.exception = throwable;
                mEventBus.post(event);
            }
        });
    }
}
