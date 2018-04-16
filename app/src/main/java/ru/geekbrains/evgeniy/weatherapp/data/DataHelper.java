package ru.geekbrains.evgeniy.weatherapp.data;

import io.realm.Realm;
import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

public class DataHelper {

    public static void createOrUpdateFromObject(Realm realm, final CityModel cityModel) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(cityModel);
            }
        });
    }

    public static void deleteObject(Realm realm, final  CityModel cityModel) {
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
                if(cityModel != null)
                    cityModel.deleteFromRealm();
            }
        });
    }
}
