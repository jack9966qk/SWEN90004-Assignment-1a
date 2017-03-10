# SWEN90004 Assignment 1a Notes

Where to put the mutual exclusion code?

Sensor needs to lock the belt (i.e. have critical section) at below:
	
```java
Bicycle b = belt.peek(3);
if (b != null && b.isTagged()) {
	robot.moveBicycleToInspector(...); // better be blocking
} else {
	wait(); // wait for belt to bring next bicycle
}
```

Need to use `synchronized(belt)`, otherwise this need to be an extra method on `Belt` class.

Robot: should it be a thread or a monitor?

- Thread: More intuitive. Easier to extend???
- Monitor: Simpler system, easier to implement. (Would it result in actually 2 arms if used by sensor and inspector thread???)
	- Using `synchronized` keyword on a single moving method can achieve the desired effect on solution 1, but for solution 2???

For situation below:

```
[  ] [td] [t-] [--] [  ]   Inspector: [  ]
```

Despite of further producing/consuming, the procedure must be as follow:

1. Sensor detect tagged bicycle at segment 3, calls robot arm
2. Robot arm moves the bicycle to inspector
3. Inspector inspects bicycle, calls robot arm
4. Robot arm moves the bicycle to belt
5. Belt moves

During 2-4, no bicycle can occupy segment 3

- How should it be implemented??
- Does it still hold for multiple arms/belts/sensors???

- Producer should:
	- wait until there's space at beginning
	- get notified when belt moves (which makes space at beginning)
- Consumer should:
	- wait until there's bicycle at end
	- get notified when belt moves (which may bring bicycle to end)
- Sensor should:
	- wait until there's bicycle at segment 3
	- wait until arm is free
	- get notified when belt moves (which may bring bicycle to segment 3)
- Robot arm should:
	- wait until current moving is complete
	- When moving to belt:
		- wait until there's space at segment 3 / inspector
		- get notified when belt moves (which may make space at segment 3)
	- notify belt/inspector bicycle *arrives* (and only for arrival, if arm actually holds bicycle)
- Inspector should:
	- When empty:
		- wait for bicycle to come
	- When finished inspecting:
		- wait until bicycle is removed
- Belt mover should:
	- wait for producer, consumer, sensor, arm to finish putting/getting/checking bicycle at belt
	- notify producer, consumer, sensor after belt moves

States:

- Producer:
	- Sleep
	- Waiting for space
	- Putting bicycle (instant)
- Consumer:
	- Sleep
	- Waiting for bicycle
	- Taking bicycle (instant)
- Sensor:
	- Waiting for bicycle
	- Check bicycle tag and call arm (instant?)
- Arm:
	- Take bicycle from belt
	- Take bicycle from inspector
	- Waiting for belt space (should never happen for 1a??)
	- Waiting for inspector space (should never happen for 1a??)
	- Waiting for moving job
	- Moving bicycle (Sleep)
- Inspector:
	- Waiting for bicycle to come
	- Waiting for bicycle to be removed
	- Inspecting (Sleep)
- Belt mover:
	- Sleep
	- Move belt (instant)


