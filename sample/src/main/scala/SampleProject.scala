import com.geishatokyo.sqlgen.Project

/**
  * Created by takezoux2 on 2016/08/05.
  */
class SampleProject extends Project{

  onSheet("Hoge"){ implicit sheet =>
    rows.foreach(row => row("name") = "ID:" + row("id").asString)
    column("ig").ignore
  }

  onSheet("Fuga"){implicit sheet =>
    column("id") := column("name").asString
  }


}
