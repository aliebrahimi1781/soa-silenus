<?php

$manifest = array (
	'acceptable_sugar_versions' => array (),
	'acceptable_sugar_flavors' =>	array( 'CE', 'PRO','ENT' ),
	'readme'=>'Silenus Consultoría, S.L.',
	'key' => 'soa',
	'author' => 'Silenus',
	'description' => 'Silenus Consultoría, S.L.',
	'icon' => '',
	'is_uninstallable' => true,
	'name' => 'soa',
	'published_date' => '',
	'type' => 'module',
	'version' => '1.0',
	'remove_tables' => 'prompt',
);


$installdefs = array (
  'id' => 'soa',
  'copy' =>
  array (
    0 =>
    array (
      'from' => '<basepath>/include',
      'to' => 'custom/include',
    ),
  ),
  'logic_hooks' => array(
     array(
			'module'         => 'Accounts',
			'hook'           => 'after_save',
			'order'          => 1,
			'description'    => 'Send JMS message on save',
			'file'           => 'custom/include/hooks/AccountHooks.php',
			'class'          => 'AccountHooks',
			'function'       => 'after_save',
     ),
     array(
			'module'         => 'Opportunities',
			'hook'           => 'after_save',
			'order'          => 1,
			'description'    => 'Send JMS message on save',
			'file'           => 'custom/include/hooks/OpportunityHooks.php',
			'class'          => 'OpportunityHooks',
			'function'       => 'after_save',
     ),
  )
);

?>