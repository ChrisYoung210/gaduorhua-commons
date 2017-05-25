package org.wulfnoth.gadus.commons.collection

import java.util.concurrent.TimeoutException

import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.language.postfixOps

/**
  * @author young
  * The primary function of BlockingTable is similar to Map.
  *
  * However, the most important function that BlockingTable provide is the blocked operation.
  *
  * if the corresponding K/V has been putted, the value will be returned immediately when the
  * get(key: K) method been called. Otherwise, the get method will return the value
  * until the corresponding K/V been putted or throw a TimeoutException.
  */
class BlockingTable[K, V](var timeoutMillis : Long) {


	private class AsyncResult {

		var element : V = _
		private var available = false

		@throws(classOf[TimeoutException]) def get : V = {
			synchronized {
				if (available) element
				else {
					wait(timeoutMillis)
					if (available) element
					else throw new TimeoutException
				}
			}
		}

		def set(value : V) {
			synchronized {
				element = value
				available = true
				notify()
				this
			}
		}
	}

	private val elements = new mutable.HashMap[K, AsyncResult]

	/**
	  * get the value that the key corresponding.
	  * if the K/V tuple already put into the BlockingTable, this method will return the
	  * value immediately. Or else block the thread until the K/V tuple been putted or throw
	  * a java.util.concurrent.TimeoutException
	  *
	  * @param key the key whose associated value is to be returned
	  * @return the value to which the specified key is mapped,
	  *         or null if this map contains no mapping for the key
	  */
	def get(key : K): V = {
		BlockingTableStatic.LOG debug s"Try get $key"
		val result = synchronized {
			elements getOrElseUpdate(key, new AsyncResult)
		}
		val value = result.get
		elements.remove(key)
		value
	}

	/**
	  * Associates the specified value with the specified key in this map.
      * If the map previously contained a mapping for the key, the old value
      * is replaced.
      *
      * @param key key with which the specified value is to be associated
      * @param value value to be associated with the specified key
      */
	def put(key : K, value : V) {
		BlockingTableStatic.LOG debug s"PUT $key"
		synchronized {
			elements getOrElseUpdate(key, new AsyncResult) set value
		}
	}
}

private object BlockingTableStatic {
	val LOG = LoggerFactory getLogger getClass
}