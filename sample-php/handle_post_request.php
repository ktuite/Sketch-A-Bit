<?php
    include_once("config.php");
    if (isset($_FILES['photo']) && ($_FILES['photo']['size'] > 0)){
        $name = tempnam("pix","");
        move_uploaded_file($_FILES['photo']['tmp_name'], $name);
        rename($name, $name.".png");
        $basename = basename($name, ".png");
        
        mysql_connect($host, $user, $pass);
        mysql_select_db($database);


        /* Request includes the tmpname of the original image
         * we look that up and get its id
         * and set the parent of the new picture to the 
         * original picture's id 
         */
        $parent = 0;
        if (isset($_REQUEST['parent'])){
            $parent_name = $_REQUEST['parent'];
            $query = "select id from sketches where name='".$parent_name."'";
            $res = mysql_query($query);
            if ($data = mysql_fetch_row($res)){
                $parent = $data[0]; 
            }
        }
        
        /* Insert this new name/parent combo into the databasse
         * where it will get assigned a new unique id
         */
        $query = "insert into sketches (name, parent) 
            VALUES ('".$basename."','".$parent."')";
        mysql_query($query);

        
    }
    else {
        mysql_connect($host, $user, $pass);
        mysql_select_db($database);

        $query = "select * from sketches";
        print($query);
        $res = mysql_query($query);
        while($obj = mysql_fetch_object($res)){
            print_r($obj);
        }
        
        die("mrraahhhhuuugggghhhhhhhhh");
    }
?>
