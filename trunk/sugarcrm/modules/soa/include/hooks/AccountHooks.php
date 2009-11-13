<?php

if(!defined('sugarEntry') || !sugarEntry) die('not a valid entry point');

require_once('custom/include/hooks/BaseHooks.php');

class AccountHooks extends BaseHooks {

	function after_save(&$bean, $event, $arguments) {
		$this->send_bean($bean, "/queue/crm-account");
	}

};

?>