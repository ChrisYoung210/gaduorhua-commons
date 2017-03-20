package com.young.commons.util

import scala.language.postfixOps

/**
  * @author Young
  *
  * This is a Reflections relative utility object
  */
object Reflections {

  /**
    * get a new instance of wanted class with no-args constructor
    * @param clazz the Class instance of which class object wanted
    * @return the new instance
    */
  def newInstance[T](clazz : Class[T]): T = newInstance(clazz, null)

  private def newInstance[T](clazz: Class[T], args: Array[_]): T = {

    if (args == null || args.isEmpty) {
      val constructor = clazz.getConstructor()
      constructor.setAccessible(true)
      constructor.newInstance()
    } else {
      null.asInstanceOf[T]
    }
      /*val constructors = clazz getDeclaredConstructors()

      val result = constructors filter ( x => {
        if (args.length == x.getParameterCount) {
          val parameters = x.getParameterTypes
          args.indices foreach (y => println(parameters(y) + "\n" + args(y).getClass))
          !(args.indices exists (y => !parameters(y).isAssignableFrom(args(y).getClass)))
        } else false
      })
      println(result.length)
      val constructor = {
        if (result.length == 0)
          throw new RuntimeException(s"no constructor found when construct type of $clazz")
        else if (result.length == 1)
          result(0)
        else
          throw new RuntimeException(s"ambiguous constructor found when construct type of $clazz")
      }

      constructor setAccessible true
      constructor.newInstance(args).asInstanceOf*/
  }
}