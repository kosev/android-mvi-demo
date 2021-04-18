package net.kosev.mvidemo.repository

import kotlinx.coroutines.delay

suspend fun simulateNetworkLatency(millis: Long): Unit = delay(millis)
