/**
* This file is part of Glue: Adhesive BRMS
*
* Copyright (c)2012 José Luis Granda <jlgranda@eqaula.org> (Eqaula Tecnologías Cia Ltda)
* Copyright (c)2012 Eqaula Tecnologías Cia Ltda (http://eqaula.org)
*
* If you are developing and distributing open source applications under
* the GNU General Public License (GPL), then you are free to re-distribute Glue
* under the terms of the GPL, as follows:
*
* GLue is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Glue is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Glue. If not, see <http://www.gnu.org/licenses/>.
*
* For individuals or entities who wish to use Glue privately, or
* internally, the following terms do not apply:
*
* For OEMs, ISVs, and VARs who wish to distribute Glue with their
* products, or host their product online, Eqaula provides flexible
* OEM commercial licenses.
*
* Optionally, Customers may choose a Commercial License. For additional
* details, contact an Eqaula representative (sales@eqaula.org)
*/
package edu.sgssalud.rewrite;

import javax.servlet.ServletContext;
import org.ocpsoft.common.services.NonEnriching;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;



/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ProjectRewriteConfiguration extends HttpConfigurationProvider implements NonEnriching
{
//   private static final String PROJECT = "[a-zA-Z0-9-]+";

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder
               .begin().defineRule();


//               .defineRule()
//               .when(Direction.isInbound().andNot(Path.matches(".*xhtml")).and(
//                        Path.matches("/{profile}/{project}")
//
//                                 .where("profile")
//                                 .constrainedBy(new RegexConstraint("(?=.*[A-Z]+.*).*"))
//                                 .transformedBy(new ToLowerCase())

//                                 .where("project")
//                                 .constrainedBy(new RegexConstraint("(?=.*[A-Z]+.*).*"))
//                                 .transformedBy(new ToLowerCase())))
//               .perform(Redirect.permanent(context.getContextPath() + "/{profile}/{project}"))
//
//               /*
//                * Transform Profile Name and Project Slug
//                */
//               .defineRule()
//               .when(Direction.isInbound().andNot(Path.matches(".*xhtml")).and(
//                        Path.matches("/{profile}/{project}{tail}")
//                                 .where("profile")
//                                 .constrainedBy(new RegexConstraint("(?=.*[A-Z]+.*).*"))
//                                 .transformedBy(new ToLowerCase())
//
//                                 .where("project")
//                                 .constrainedBy(new RegexConstraint("(?=.*[A-Z]+.*).*"))
//                                 .transformedBy(new ToLowerCase())
//                                 .where("tail").matches("/.*")))
//               .perform(Redirect.permanent(context.getContextPath() + "/{profile}/{project}{tail}"))
//
//               /*
//                *  Bind profile and project value to EL & Load current Project
//                */
//               .defineRule()
//               .when(Direction.isInbound()
//                        .andNot(Path.matches(".*xhtml"))
//                        .and(Path.matches("/{profile}.*").where("profile")
//                                 .bindsTo(El.property("params.profileUsername"))))
//               .perform(new Operation() {
//                  @Override
//                  public void perform(final Rewrite event, final EvaluationContext context)
//                  {}
//               })
//               /**
//                * <p/>

//                * perhaps a database validation or constraint? on the above profile property injection?
//                */
//               .addRule(Join.path("/{profile}").to("/pages/profile.xhtml")
//                        .when(Not.any(Resource.exists("/pages/{profile}.xhtml")))
//                        .perform(PhaseAction.retrieveFrom(El.retrievalMethod("profiles.verifyExists"))))
//
//               .defineRule()
//               .when(
//                        Direction.isInbound()
//                                 .andNot(Path.matches(".*xhtml"))
//                                 .and(Direction.isInbound())
//                                 .and(Path.matches("/{profile}/{project}.*")
//                                          .where("profile").matches(PROJECT)
//                                          .where("project").matches(PROJECT)
//                                          .bindsTo(El.property("params.projectSlug")))
//               )
//               .perform(PhaseAction.retrieveFrom(El.retrievalMethod("projects.loadCurrent")))
//
//               /*
//                *  Project Pages
//                */
//               .addRule(Join.path("/{profile}/{project}")
//                        .to("/pages/project/view.xhtml")
//                        .where("project").matches(PROJECT)
//                        .when(GlueResources.excluded()).perform(null)
//               )
//
//               .addRule(Join.path("/{profile}/{project}/issues")
//                        .to("/pages/project/backlog.xhtml")
//                        .where("project").matches(PROJECT)
//                        .when(GlueResources.excluded())
//               )
//
//               /*
//                *  Story pages
//                */
//               .addRule(Join.path("/{profile}/{project}/stories/new")
//                        .to("/pages/story/create.xhtml")
//                        .where("project").matches(PROJECT)
//                        .when(GlueResources.excluded())
//               )
//
//               /*
//                * Bind story number to EL and Load current story
//                */
//               .defineRule()
//               .when(
//                        Direction.isInbound()
//                                 .andNot(Path.matches(".*xhtml"))
//                                 .and(Direction.isInbound())
//                                 .and(Path.matches("/{profile}/{project}/story/{story}.*")
//                                          .where("profile").matches(PROJECT)
//                                          .where("project").matches(PROJECT)
//                                          .where("story").matches(PROJECT)
//                                          .bindsTo(El.property("params.storyNumber")))
//               )
//               .perform(PhaseAction.retrieveFrom(El.retrievalMethod("stories.loadCurrent")))
//
//               .addRule(Join.path("/{profile}/{project}/story/{story}")
//                        .to("/pages/story/view.xhtml")
//                        .where("project").matches(PROJECT)
//                        .when(GlueResources.excluded())
//               )
//
//               /*
//                *  Iteration pages
//                */
//               /*
//                * Bind iteration number to EL and Load current story
//                */
//               .defineRule()
//               .when(
//                        Direction.isInbound()
//                                 .andNot(Path.matches(".*xhtml"))
//                                 .and(Direction.isInbound())
//                                 .and(Path.matches("/{profile}/{project}/iteration/{iteration}.*")
//                                          .where("profile").matches(PROJECT)
//                                          .where("project").matches(PROJECT)
//                                          .where("iteration").matches(PROJECT)
//                                          .constrainedBy(new IntegerConstraint(1, null))
//                                          .bindsTo(El.property("params.iterationNumber")))
//               )
//               .perform(PhaseAction.retrieveFrom(El.retrievalMethod("iterations.loadCurrent")))
//
//               .addRule(Join.path("/{profile}/{project}/iteration/{iteration}")
//                        .to("/pages/iteration/sorter.xhtml")
//                        .where("project").matches(PROJECT)
//                        .where("iteration").matches("\\d+")
//                        .constrainedBy(new IntegerConstraint(1, null))
//                        .when(GlueResources.excluded()).withId("iteration-view")
//               )
//
//      ;

   }

   @Override
   public int priority()
   {
      return 20;
   }
}
