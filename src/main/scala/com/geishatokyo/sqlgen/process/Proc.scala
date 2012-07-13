package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.Project
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.logger.Logger

/**
 *
 * User: takeshita
 * Create: 12/07/12 17:25
 */

trait Proc {

  def name : String

  def apply(workbook : Workbook) : Workbook

  def then( proc : Proc) : Proc = {
    new SeqProc(List(this,proc))
  }

  def split( mainStream : Proc, subStream : Proc) : Proc = {
    new SplitProc(mainStream,subStream)
  }

  def skipOnError : Proc = {
    if(this.isInstanceOf[ErrorSkipProc]){
      this
    }else{
      new ErrorSkipProc(this)
    }
  }
}

class ErrorSkipProc(innerProc : Proc) extends Proc {
  def name: String = innerProc.name

  def apply(workbook: Workbook): Workbook = {
    val cp = workbook.copy()
    try{
      innerProc(cp)
    }catch{
      case e : Exception => {
        Logger.log("Exception occured while processing %s".format(name),e)
        workbook
      }
    }
  }
}

case class SeqProc(var processes : List[Proc]) extends Proc {

  def name = "SequenceProcess"


  def apply(workbook: Workbook): Workbook = {
    processes.foldLeft(workbook)( (wb,proc) => proc(wb))
  }

  override def then(proc: Proc): Proc = {
    proc match{
      case SeqProc(procs) => {
        this.processes = this.processes ::: procs
      }
      case p => this.processes :+= p
    }
    this
  }
}

case class SplitProc(main : Proc,sub : Proc) extends Proc{
  def name: String = "SplitProc"

  def apply(workbook: Workbook): Workbook = {
    val cp = workbook.copy()
    sub(cp)
    main(workbook)
  }
}
