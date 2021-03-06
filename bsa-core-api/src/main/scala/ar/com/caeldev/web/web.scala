package ar.com.caeldev.web

import ar.com.caeldev.api.Api
import ar.com.caeldev.core.{ CoreActors, Core }

/** Provides the web server (spray-can) for the REST api in ``Api``, using the actor system
 *  defined in ``Core``.
 *
 *  You may sometimes wish to construct separate ``ActorSystem`` for the web server machinery.
 *  However, for this simple application, we shall use the same ``ActorSystem`` for the
 *  entire application.
 *
 *  Benefits of separate ``ActorSystem`` include the ability to use completely different
 *  configuration, especially when it comes to the threading model.
 */
trait Web {
  this: Api with CoreActors with Core =>

}
