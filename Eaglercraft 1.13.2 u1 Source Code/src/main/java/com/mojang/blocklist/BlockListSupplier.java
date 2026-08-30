package com.mojang.blocklist;

import java.util.function.Predicate;

public interface BlockListSupplier {
	Predicate<String> createBlockList();
}
