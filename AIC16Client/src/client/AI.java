
// The secret side of me,
// I never let you see
// I keep it caged
// But I can't control it
// So stay away from me
// The beast is ugly
// I feel the rage
// And I just can't hold it
// It's scratching on the walls
// In the closet, in the halls
// It comes awake
// And I can't control it
// Hiding under the bed
// In my body, in my head
// Why won't somebody come and save me from this?
// Make it end!
//
// I feel it deep within,
// It's just beneath the skin
// I must confess that I Feel like a monster
// I hate what I've become
// The nightmare's just begun
// I must confess that I feel like a monster
// I, I feel like a monster
// I, I feel like a monster
//
// My secret side I keep
// Hid under lock and key
// I keep it caged
// But I can't control it
// Cause if I let him out
// He'll tear me up
// And break me down
// Why won't somebody come and save me from this?
// Make it end!
//
//
//
//
//
// I feel it deep within,
// It's just beneath the skin
// I must confess that I Feel like a monster
// I hate what I've become
// The nightmare's just begun
// I must confess that I feel like a monster
// I feel it deep within,
// It's just beneath the skin
// I must confess that I feel like a monster
// I, I feel like a monster
// I, I feel like a monster
//
// It's hiding in the dark
// It's teeth are razor sharp
// There's no escape for me
// It wants my soul,
// It wants my heart
// No one can hear me scream
// Maybe it's just a dream
// Or maybe it's inside of me
// Stop this monster!
//
// I feel it deep within,
// It's just beneath the skin
// I must confess that I
// (Feel like a monster)
// I hate what I've become
// The nightmare's just begun
// I must confess that I feel like a monster
// I feel it deep within,
// It's just beneath the skin
// I must confess that I feel like a monster
// I gotta lose control
// Here's something radical
// I must confess that I feel like a monster
// I, I feel like a monster
// I, I feel like a monster
// I, I feel like a monster
// I, I feel like a monster
package client;

import java.util.HashMap;
import java.util.Map;

import moreda.Processor;
import moreda.processors.Processor_base;

/**
 * AI class. You should fill body of the method {@link #doTurn}. Do not change
 * name or modifiers of the methods or fields and do not add constructor for
 * this class. You can add as many methods or fields as you want! Use world
 * parameter to access and modify game's world! See World interface for more
 * details.
 */
public class AI {
	private Map<String, Processor> processors;

	public AI() {
		processors = new HashMap<>();
		// add any Performance HERE:
		/*
		 * per1 as first performance
		 */
		Processor processor = new Processor_base();
		processors.put(processor.getName(), processor);

	}

	public void doTurn(World world) {
		processors.get("pro1").doTurn(world);
	}

}