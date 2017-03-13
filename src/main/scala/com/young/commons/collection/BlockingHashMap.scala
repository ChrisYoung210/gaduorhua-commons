package com.young.commons.collection

import java.util.concurrent.TimeoutException

import scala.collection.mutable.HashMap
import scala.language.postfixOps

/**
  * Created by Young on 16-9-5.
  */
class BlockingHashMap[K, V](var timeoutMillis : Long) {

  class AsyncResult {
    var element : V = _
    private var available = false

    def get : V = {
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



  val elements = new HashMap[K, AsyncResult]

  def get(key : K) = {
    val result = elements getOrElseUpdate(key, new AsyncResult)
    val value = result get;
    elements.remove(key)
    value
  }

  def put(key : K, value : V) {
    elements getOrElseUpdate(key, new AsyncResult) set value
  }

}

