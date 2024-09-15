package io.github.alekso56.MorrisInfinity.state;

import org.bukkit.entity.BlockDisplay;

public class DisplayPoolEntry {
         public BlockDisplay display;
         public int x = 0;
         public int y = 0;
         
		public DisplayPoolEntry(BlockDisplay display, int x, int y) {
			this.display = display;
			this.x = x;
			this.y = y;
		}
		private DisplayPoolEntry() {}
         
}
