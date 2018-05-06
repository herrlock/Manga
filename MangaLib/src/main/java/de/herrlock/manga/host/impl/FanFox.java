package de.herrlock.manga.host.impl;

import com.google.auto.service.AutoService;

import de.herrlock.manga.host.HosterImpl;
import de.herrlock.manga.host.annotations.Details;

@AutoService( HosterImpl.class )
@Details( name = "Mangafox", baseUrl = "http://www.fanfox.net/" )
public final class FanFox extends MangaFox {
    // no own implementation
}
