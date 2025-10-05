package com.example.accelerometertutorial

class StepCounter {
    private var lastStep : Long = 0
    private var steps : Int = -1 //To account for initial jump from 0

    fun StepChecker(mag: Float, time: Long): Int {
        if((mag > 2.5 || mag < -2.5) && time > (lastStep + 1500)) {
            steps++
            lastStep = System.currentTimeMillis()
        }
        return(steps)
    }
}