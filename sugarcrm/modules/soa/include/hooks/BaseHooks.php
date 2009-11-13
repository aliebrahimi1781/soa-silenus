<?php


if(!defined('sugarEntry') || !sugarEntry) die('not a valid entry point');

require_once('custom/include/Stomp.php');


class BaseHooks {


	function is_serializable($field_type) {
		switch($field_type) {
			case 'link':
				return false;
			default:
				return true;
		}
	}


	function serialize_type($field_type, $field_value) {
 		switch($field_type) {
 			case 'datetime':
 				// YYYY-MM-DDThh:mm:ss  --- 2009-11-06 10:50:20
 				// return strftime('%Y-%m-%dT%H:%M%S', $field_value);
 				return str_replace(' ', 'T', $field_value);
 			default:
 				return $field_value;
 		}
	}


	function serialize_bean(&$bean) {

		$result = "<{$bean->object_name}>";

    $field_name = '';
    $field_type = '';
    $field_value = '';
    $serialized_value = '';
		foreach ($bean->field_defs as $field=>$value) {
      $field_name = $value['name'];
      $field_type = $value['type'];
      $field_value = $bean->$field;

      if( $this->is_serializable($field_type) ) {
      	$serialized_value = $this->serialize_type($field_type, $field_value);
      	$result = $result . "<{$field_name}>{$serialized_value}</{$field_name}>";
      }

		}


		$result = $result . "</{$bean->object_name}>";

		return $result;
	}


	function send_bean(&$bean, $queue_name) {
		global $sugar_config;

		$stomp_url = "tcp://localhost:40001";
		if( !empty( $sugar_config['stomp_url'] ) ) {
			$stomp_url = $sugar_config['stomp_url'];
		}

		// make a connection
		$con = new Stomp($stomp_url);
		// connect
		$con->connect();

		// send a message to the queue
		$con->send($queue_name, $this->serialize_bean($bean));

    // disconnect
		$con->disconnect();
	}


};



?>