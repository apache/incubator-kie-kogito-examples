/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * serializeToJSON jQuery plugin
 * https://github.com/raphaelm22/jquery.serializeToJSON
 * @version: v1.4.1 (October, 2019)
 * @author: Raphael Nunes
 *
 * Created by Raphael Nunes on 2015-08-28.
 *
 * Licensed under the MIT (http://www.opensource.org/licenses/mit-license.php)
 */

(function (factory) {
    if (typeof define === 'function' && define.amd) {
        define(['jquery'], factory);
    } else if (typeof module === 'object' && module.exports) {
        module.exports = function( root, jQuery ) {
            if ( jQuery === undefined ) {
                if ( typeof window !== 'undefined' ) {
                    jQuery = require('jquery');
                }
                else {
                    jQuery = require('jquery')(root);
                }
            }
            factory(jQuery);
            return jQuery;
        };
    } else {
        factory(jQuery);
    }
}(function ($) {
	'use strict';

    $.fn.serializeToJSON = function(options) {

		var f = {
			settings: $.extend(true, {}, $.fn.serializeToJSON.defaults, options),

			getValue: function($input) {
				var value = $input.val();

			    if ($input.is(":radio")) {
			        value = $input.filter(":checked").val() || null;
			    }

			    if ($input.is(":checkbox")) {
			        value = $($input).prop('checked');
			    }

				if (this.settings.parseBooleans) {
					var boolValue = (value + "").toLowerCase();
					if (boolValue === "true" || boolValue === "false") {
						value = boolValue === "true";
					}
				}

				var floatCondition = this.settings.parseFloat.condition;
				if (floatCondition !== undefined && (
				    (typeof(floatCondition) === "string"   && $input.is(floatCondition)) ||
				    (typeof(floatCondition) === "function" && floatCondition($input)))) {

					value = this.settings.parseFloat.getInputValue($input);
					value = Number(value);

                    if (this.settings.parseFloat.nanToZero && isNaN(value)){
                        value = 0;
                    }
                }

				return value;
			},

			createProperty: function(o, value, names, $input) {
				var navObj = o;

				for (var i = 0; i < names.length; i++) {
					var currentName = names[i];

					if (i === names.length - 1) {
						var isSelectMultiple = $input.is("select") && $input.prop("multiple");

						if (isSelectMultiple && value !== null){
							navObj[currentName] = new Array();

							if (Array.isArray(value)){
								$(value).each(function() {
									navObj[currentName].push(this);
								});
							}
							else{
								navObj[currentName].push(value);
							}
						} else if(typeof navObj[currentName] !== "undefined"){
							if (!$input.is("[type='hidden']"))
								navObj[currentName] = value;
						} else {
							navObj[currentName] = value;
						}
					} else {
						var arrayKey = /\[\w+\]/g.exec(currentName);
						var isArray = arrayKey != null && arrayKey.length > 0;

						if (isArray) {
							currentName = currentName.substr(0, currentName.indexOf("["));

							if (this.settings.associativeArrays) {
								if (!navObj.hasOwnProperty(currentName)) {
									navObj[currentName] = {};
								}
							} else {
								if (!Array.isArray(navObj[currentName])) {
									navObj[currentName] = new Array();
								}
							}

							navObj = navObj[currentName];

							var keyName = arrayKey[0].replace(/[\[\]]/g, "");
							currentName = keyName;
						}

						if (!navObj.hasOwnProperty(currentName)) {
							navObj[currentName] = {};
						}

						navObj = navObj[currentName];
					}
				}
			},

			includeUncheckValues: function(selector, formAsArray){
				$(":radio", selector).each(function(){
					var isUncheckRadio = $("input[name='" + this.name + "']:radio:checked").length === 0;
					if (isUncheckRadio)
					{
						formAsArray.push({
							name: this.name,
							value: null
						});
					}
				});

				$("select[multiple]", selector).each(function(){
					if ($(this).val() === null){
						formAsArray.push({
							name: this.name,
							value: null
						});
					}
				});
			},

			//clone of the jquery method, but returns the element
			serializeArray: function(formSelector) {
				var rCRLF = /\r?\n/g,
					rsubmitterTypes = /^(?:submit|button|image|reset|file)$/i,
					rsubmittable = /^(?:input|select|textarea|keygen)/i,
					rcheckableType = ( /^(?:checkbox|radio)$/i );

				return formSelector.map(function() {
					var elements = jQuery.prop( this, "elements" );
					return elements ? jQuery.makeArray( elements ) : this;
				})
				.filter( function() {
					var type = this.type;
					return this.name && !jQuery( this ).is( ":disabled" ) &&
						rsubmittable.test( this.nodeName ) && !rsubmitterTypes.test( type ) &&
						( this.checked || !rcheckableType.test( type ) );
				})
				.map( function( i, elem ) {
					var val = jQuery( this ).val();

					if ( val == null ) return null;

					if ( Array.isArray( val ) ) {
						return jQuery.map( val, function( val ) {
							return { name: elem.name, value: val.replace( rCRLF, "\r\n" ), elem: elem };
						} );
					}

					return { name: elem.name, value: val.replace( rCRLF, "\r\n" ), elem: elem };
				}).get();
			},

			serializer: function(selector) {
				var self = this;

				var formAsArray = this.serializeArray($(selector));
				this.includeUncheckValues(selector, formAsArray);

				var serializedObject = {}

				for (var prop in formAsArray) {
					if(formAsArray.hasOwnProperty( prop )) {
						var item = formAsArray[prop];

						var $input = $(item.elem);

						 var value = self.getValue($input);
						 var names = item.name.split(".");

						self.createProperty(serializedObject, value, names, $input);
					}
				}
				return serializedObject;
			}
		};

		return f.serializer(this);
    };

	$.fn.serializeToJSON.defaults = {
        associativeArrays: true,
        parseBooleans: true,
		parseFloat: {
			condition: undefined,
			nanToZero: true,
			getInputValue: function($input){
				return $input.val().split(",").join("");
			}
		}
    };
}));