package com.github.foxy.common.config.section;

import com.github.foxy.common.config.IMappingStorage;
import com.github.foxy.common.config.IStoredSectionPositionIterator;
import com.github.foxy.common.world.WorldSection;

public abstract class SectionStorage implements IMappingStorage, IStoredSectionPositionIterator {
    public abstract int loadSection(WorldSection into);

    public abstract void saveSection(WorldSection section);
}
