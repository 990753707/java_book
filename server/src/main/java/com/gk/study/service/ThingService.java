package com.gk.study.service;


import com.gk.study.entity.Thing;

import java.util.List;

public interface ThingService {
    List<Thing> getThingList(String keyword, String sort, String c, String tag);
    void createThing(Thing thing);
    void deleteThing(long id);

    void updateThing(Thing thing);

    Thing getThingById(long id);

    void addWishCount(long thingId);

    void addCollectCount(long thingId);
}
