package ru.greenpix.civilization.guises;

import ru.greenpix.developer.utils.guises.GuiseItem.GuiseItemAction;
import ru.greenpix.developer.utils.items.Item;

public interface GuiseElement {

	Item getIcon();
	
	GuiseItemAction getClickAction(String type);
	
	GuiseItemAction getLoadAction(String type);
	
}
