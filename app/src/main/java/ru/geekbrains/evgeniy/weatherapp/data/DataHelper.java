package ru.geekbrains.evgeniy.weatherapp.data;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;
import ru.geekbrains.evgeniy.weatherapp.model.CityModelArray;
import ru.geekbrains.evgeniy.weatherapp.ui.home.adapters.CustomElementsAdapter;

public class DataHelper {

    public static void setFavoriteWhereNoFavoriteSync(Realm realm) {
        CityModel favoriteCity = realm.where(CityModel.class).equalTo(CityModel.FAVORITE_FIELD, true).findFirst();
        if (favoriteCity == null) {
            CityModel cm = realm.where(CityModel.class).findFirst();
            if (cm != null)
                cm.setFavorite(true);
        }
    }

    public static void createOrUpdateFromObject(Realm realm, final CityModel cityModel) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                createOrUpdateFromObjectSync(realm, cityModel);
            }
        });
    }

    public static void updateAllByList(Realm realm, final ArrayList<CityModel> cityModelArray) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (CityModel cm: cityModelArray) {
                    createOrUpdateFromObjectSync(realm, cm);
                }
                setFavoriteWhereNoFavoriteSync(realm);
            }
        });
    }

    public static void createOrUpdateFromObjectSync(Realm realm, CityModel cityModel) {
        CityModel currentCM = realm.where(CityModel.class).equalTo(CityModel.FIELD_ID, cityModel.id).findFirst();
        Long oldSortId = null;
        boolean isFavorite = false;
        if(currentCM != null) {
            oldSortId = currentCM.sortId;
            isFavorite = currentCM.isFavorite();
        }
        CityModel newCM = realm.copyToRealmOrUpdate(cityModel);
        newCM.sortId = oldSortId;
        if (newCM.sortId == null) {
            Number maxSortId = realm.where(CityModel.class).max(CityModel.SORT_ID);
            newCM.sortId = (maxSortId == null) ? 1 : maxSortId.longValue() + 1;
        }
        if (newCM.isFavorite() != isFavorite) {
            newCM.setFavorite(isFavorite);
        }
    }

    public static void updateSortIDs(Realm realm) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<CityModel> realmResults = realm.where(CityModel.class).findAll().sort(CityModel.SORT_ID);
                Long sortId = Long.valueOf(1);
                for (CityModel cm: realmResults) {
                    cm.sortId = sortId;
                    sortId++;
                }
            }
        });
    }

    public static void setFavorite(Realm realm, CityModel cityModel) {
        final Long id = cityModel.id;
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<CityModel> realmResults = realm.where(CityModel.class).findAll();
                for (CityModel cm: realmResults) {
                    if(cm.id.equals(id))
                        cm.setFavorite(true);
                    else
                        cm.setFavorite(false);
                }
            }
        });
    }

    public static void deleteObject(Realm realm, final CityModel cityModel) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (cityModel != null) {
                    cityModel.deleteFromRealm();
                    setFavoriteWhereNoFavoriteSync(realm);
                }
            }
        });
    }

    public static void deleteObjectById(Realm realm, final Long id) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CityModel cityModel = realm.where(CityModel.class).equalTo(CityModel.FIELD_ID, id).findFirst();
                if (cityModel != null) {
                    cityModel.deleteFromRealm();
                    setFavoriteWhereNoFavoriteSync(realm);
                }
            }
        });
    }

    public static void clear(Realm realm) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }

    public static void editNameById(Realm realm, final Long id, final String name) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CityModel cityModel = realm.where(CityModel.class).equalTo(CityModel.FIELD_ID, id).findFirst();
                if (cityModel != null)
                    cityModel.setName(name);
            }
        });
    }

    public static String getIDsSync(Realm realm) {
        RealmResults<CityModel> realmResults = realm.where(CityModel.class).findAll().sort(CityModel.SORT_ID);
        List<String> ids = new ArrayList<>();
        for (CityModel cm: realmResults) {
            ids.add(String.valueOf(cm.id));
        }
        return StringUtils.join(ids, ",");
    }

    public static void updateAllWeathers(Realm realm) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<CityModel> realmResults = realm.where(CityModel.class).findAll().sort(CityModel.SORT_ID);
                List<String> ids = new ArrayList<>();
                for (CityModel cm: realmResults) {
                    ids.add(String.valueOf(cm.id));
                }
                CityModelArray cityModelArray = WeatherDataLoader.getListWeatherByIDs(StringUtils.join(ids, ","));
                if(cityModelArray != null && cityModelArray.list.size() > 0) {
                    for (CityModel cm: cityModelArray.list) {
                        createOrUpdateFromObjectSync(realm, cm);
                    }
                }
            }
        });
    }

    public static CityModel getFavoriteCitySync(Realm realm) {
        return realm.where(CityModel.class).equalTo(CityModel.FAVORITE_FIELD, true).findFirst();
    }

    public static RealmResults<CityModel> getDataForWidget(Realm realm) {
        return realm.where(CityModel.class).findAll().sort(CityModel.FAVORITE_FIELD).sort(CityModel.SORT_ID);
    }
}