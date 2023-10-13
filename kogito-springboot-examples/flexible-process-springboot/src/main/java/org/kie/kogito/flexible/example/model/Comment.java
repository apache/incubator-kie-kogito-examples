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
package org.kie.kogito.flexible.example.model;

import java.time.ZonedDateTime;

public class Comment {

    private String author;
    private ZonedDateTime date = ZonedDateTime.now();
    private String text;

    public Comment() {
    }

    public Comment(Comment c) {
        this.author = c.author;
        this.date = c.date;
        this.text = c.text;
    }

    public String getAuthor() {
        return author;
    }

    public Comment setAuthor(String author) {
        this.author = author;
        return this;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public Comment setDate(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public String getText() {
        return text;
    }

    public Comment setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String toString() {
        return "Comment{" + "author='" + author + '\'' + ", date=" + date + ", text='" + text + '\'' + '}';
    }
}
