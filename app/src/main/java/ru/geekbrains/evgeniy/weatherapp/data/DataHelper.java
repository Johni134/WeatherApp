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

    public static void createOrUpdateFromObject(Realm realm, final CityModel cityModel) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                createOrUpdateFromObjectSync(realm, cityModel);
            }
        });
    }

    public static void createOrUpdateFromObjectSync(Realm realm, CityModel cityModel) {
        CityModel currentCM = realm.where(CityModel.class).equalTo(CityModel.FIELD_ID, cityModel.id).findFirst();
        Long oldSortId = null;
        if(currentCM != null)
            oldSortId = currentCM.sortId;
        CityModel newCM = realm.copyToRealmOrUpdate(cityModel);
        newCM.sortId = oldSortId;
        if (newCM.sortId == null) {
            Number maxSortId = realm.where(CityModel.class).max(CityModel.SORT_ID);
            currentCM.sortId = (maxSortId == null) ? 1 : maxSortId.longValue() + 1;
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

    public static void deleteObject(Realm realm, final CityModel cityModel) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (cityModel != null)
                    cityModel.deleteFromRealm();
            }
        });
    }

    public static void deleteObjectById(Realm realm, final Long id) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CityModel cityModel = realm.where(CityModel.class).equalTo(CityModel.FIELD_ID, id).findFirst();
                if (cityModel != null)
                    cityModel.deleteFromRealm();
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

    public static void editNameById(Realm realm, final Long id, final String name, final CustomElementsAdapter adapter) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CityModel cityModel = realm.where(CityModel.class).equalTo(CityModel.FIELD_ID, id).findFirst();
                if (cityModel != null)
                    cityModel.setName(name);
            }
        });
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
}